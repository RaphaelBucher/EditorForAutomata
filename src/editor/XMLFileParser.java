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
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

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
  private static final String stateType = "state";
  private static final String startStateType = "startState";
  private static final String endStateType = "endState";
  private static final String startEndStateType = "startEndState";
  
  /** Reads an XML-File of an Automat, builds a new Automat out of it, updates the
   * painting information of all transitions and returns the Automat. */
  public static Automat readAutomatFromXMLFile(String filePath) {
    Automat automat = new Automat();
    
    try {
      File xmlFile = new File(filePath);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(xmlFile);

      doc.getDocumentElement().normalize();

      // XML-File is invalid if the <automat> is not the root-element
      if (!doc.getDocumentElement().getNodeName().equals("automat"))
        throw new Exception();
      
      // Read all states and instantiate them
      readStates(doc, automat);
      
      // Read all transitions and instantiate them
      readTransitions(doc, automat);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    
    return automat;
  }
  
  /** Reads all transitions and their symbols of the document and instantiates them on
   * the passed Automat. 
   * @throws Exception */
  private static void readTransitions(Document doc, Automat automat) throws Exception {
    // Get all transitions
    NodeList nodeList = doc.getElementsByTagName("transition");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);

      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element transition = (Element) node;
        createTransition(transition, automat);
      }
    }
  }
  
  /** Parses a transition-node and creates the transition out of it accordingly.
   * @throws Exception */
  private static void createTransition(Element transition, Automat automat) throws Exception {
    int startStateIndex = Integer.parseInt(transition.getAttribute("startStateIndex"));
    int endStateIndex = Integer.parseInt(transition.getAttribute("endStateIndex"));
    
    State transitionStart = automat.getStateByStateIndex(startStateIndex);
    State transitionEnd = automat.getStateByStateIndex(endStateIndex);
    
    // Does the loaded automat have such states?
    if (transitionStart == null || transitionEnd == null)
      throw new Exception();
      
    Transition constructedTransition = new Transition(transitionStart, transitionEnd);
    
    // Read all symbols and add them to the constructed Transition
    readSymbols(transition, constructedTransition);
    // Needs at least one valid symbol, empty Transitions are permitted
    if (constructedTransition.getSymbols().size() <= 0)
      throw new Exception();
    
    // Add the built Transition to the Automat
    automat.addTransition(constructedTransition);
  }
  
  /** Reads all symbols of the passed transition and adds them to the transition. */
  private static void readSymbols(Element transition, Transition constructedTransition) {
    String symbolsConcatenated = transition.getElementsByTagName("symbols").item(0).getTextContent();
    
    // Iterate over all characters of the String
    for (int i = 0; i < symbolsConcatenated.length(); i++) {
      constructedTransition.addSymbol(symbolsConcatenated.charAt(i));
    }
  }
  
  /** Reads all states of the document and instantiates them on the passed Automat. 
   * @throws Exception */
  private static void readStates(Document doc, Automat automat) throws Exception {
    // Get all states
    NodeList nodeList = doc.getElementsByTagName("state");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);

      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element state = (Element) node;
        createState(state, automat);
      }
    }
  }
  
  /** Parses a state-node and creates the state out of it accordingly 
   * @throws Exception */
  private static void createState(Element state, Automat automat) throws Exception {
    int stateIndex = Integer.parseInt(state.getAttribute("stateIndex"));
    String type = state.getAttribute("type");
    int x = Integer.parseInt(state.getAttribute("x"));
    int y = Integer.parseInt(state.getAttribute("y"));
    
    // Validation checks
    if (stateIndex < 0 || x < 0 || y < 0)
      throw new Exception();
    
    // Distinguish the states type
    if (type.equals(stateType))
      automat.getStates().add(new State(stateIndex, x, y));
    else if (type.equals(startStateType))
      automat.getStates().add(new StartState(stateIndex, x, y));
    else if (type.equals(endStateType))
      automat.getStates().add(new EndState(stateIndex, x, y));
    else if (type.equals(startEndStateType))
      automat.getStates().add(new StartEndState(stateIndex, x, y));
    else
      throw new Exception();
  }
  
  /** Writes all necessary information of an Automat into an XML-File. */
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
      return startStateType;
    else if (state instanceof EndState)
      return endStateType;
    else if (state instanceof StartEndState)
      return startEndStateType;
    
    return stateType;
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
