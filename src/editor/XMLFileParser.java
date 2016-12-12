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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* --- Files structure is ---
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<automat>
    <states>
        <state stateIndex="0" type="startState" x="96" y="188" />
        <state stateIndex="1" type="state" x="232" y="142" />
        <state stateIndex="2" type="endState" x="362" y="194" />
    </states>
    <transitions>
        <transition endStateIndex="1" startStateIndex="0">
            <symbols>abc</symbols>
        </transition>
        <transition endStateIndex="2" startStateIndex="2">
            <symbols>123</symbols>
        </transition>
        <transition endStateIndex="1" startStateIndex="2">
            <symbols>lbd</symbols>
        </transition>
    </transitions>
</automat>
*/
public class XMLFileParser {
  
  public static void writeAutomatToXMLFile(Automat automat, String filePath) {
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      // <automat> as root-element
      Document doc = docBuilder.newDocument();
      Element rootElement = doc.createElement("automat");
      doc.appendChild(rootElement);

      // Append the states
      appendStates(automat.getStates(), doc, rootElement);
      
      // Append the transitions
      appendTransitions(automat.getTransitions(), doc, rootElement);
      
      // write the content into the XML file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      
      StreamResult result = new StreamResult(new File(filePath));
      transformer.transform(source, result);
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
      
      // Set the state attributes
      state.setAttribute("stateIndex", "" + statesList.get(i).stateIndex);
      state.setAttribute("type", "" + getStateType(statesList.get(i)));
      state.setAttribute("x", "" + statesList.get(i).x);
      state.setAttribute("y", "" + statesList.get(i).y);
      states.appendChild(state);
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
      transition.setAttribute("endStateIndex", "" + transitionsList.get(i).getTransitionEnd().stateIndex);
      transitions.appendChild(transition);
      
      // Add all Symbols as a child-node
      Element symbols = doc.createElement("symbols");
      symbols.appendChild(doc.createTextNode(symbolsToString(transitionsList.get(i).getSymbols())));
      transition.appendChild(symbols);
    }
  }
  
  /** Returns all symbols of the passed list as a String without spaces. */
  private static String symbolsToString(ArrayList<Symbol> symbols) {
    String concatenatedSymbols = "";
    for (int i = 0; i < symbols.size(); i++) {
      concatenatedSymbols += symbols.get(i).getSymbol();
    }
    
    return concatenatedSymbols;
  }
}
