package editor;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/** Files structure is:
 * <automat>
 *   <states>
 *     <state id="0"> // id is the stateIndex, not the ArrayLists index
 *       <type>state</type> // state, startState, endState, startEndState
 *     </state>
 *     
 *     
 *   </states>
 *  
 *   <transitions>
 *    
 *   </transitions>
 * </automat>
 * 
 *  
 *  
 *  
 *  */
public class XMLFileParser {
  
  public static void writeAutomatToXMLFile(Automat automat, String filePath) {
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      // root element
      Document doc = docBuilder.newDocument();
      Element rootElement = doc.createElement("automat");
      doc.appendChild(rootElement);

      // Append the states
      appendStates(automat.getStates(), doc, rootElement);
      
      // Append the transitions
      appendTransitions(automat.getTransitions(), doc, rootElement);

      
      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      
      
      StreamResult result = new StreamResult(new File(filePath));

      // Output to console for testing
      // StreamResult result = new StreamResult(System.out);

      transformer.transform(source, result);

      System.out.println("File saved!"); //TODO: remove

    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (TransformerException tfe) {
      tfe.printStackTrace();
    }
  }
  
  /** Appends all states */
  private static void appendStates(ArrayList<State> statesList, Document doc, Element rootElement) {
    // create surrounding states-Element
    Element states = doc.createElement("states");
    rootElement.appendChild(states);
    
    // Iterate over all states
    for (int i = 0; i < statesList.size(); i++) {
      // The state element
      Element state = doc.createElement("state");
      state.setAttribute("stateIndex", "" + statesList.get(i).stateIndex);
      states.appendChild(state);
      
      // Child-nodes
      Element type = doc.createElement("type");
      type.appendChild(doc.createTextNode(getStateType(statesList.get(i))));
      state.appendChild(type);
    }
  }
  
  /** Helper-method that returns the type of the State-instance as a String
   * @return "state", "startState", "endState" or "startEndState" */
  private static String getStateType(State state) {
    if (state instanceof StartState)
      return "startState";
    else if (state instanceof EndState)
      return "endState";
    else if (state instanceof StartEndState)
      return "startEndState";
    
    return "state";
  }
  
  /** Appends all Transitions */
  private static void appendTransitions(ArrayList<Transition> transitionsList, Document doc,
      Element rootElement) {
    // transitions
    Element transitions = doc.createElement("transitions");
    rootElement.appendChild(transitions);
    
    // Iterate over all transitions
    for (int i = 0; i < transitionsList.size(); i++) {
      // The transition element
      Element transition = doc.createElement("transition");
      transition.setAttribute("startStateIndex", "" + transitionsList.get(i).getTransitionStart().stateIndex);
      transitions.appendChild(transition);
      
      // Child-nodes
      Element symbols = doc.createElement("symbols");
      transition.appendChild(symbols);
    }
  }
}
