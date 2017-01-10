### Voraussetzungen ohne Menueintrag
#### Ist Automat NEA? (DONE), DEA? (DONE), minimaler DEA (TODO)?
-(min. DEAs sind DEAs), DEAs sind immer auch NEAs, und NEAs sind immer auch Epsilon-Automaten (einfach ohne Epsilon-Übergänge)  

###Voraussetzungen mit Menueintrag
#### Layout-Algorithmus eines Automaten (DONE)
-in Klasse Layout  
-Eine Idee wäre Zustände in versetzten Linien anzuordnen, Zustände mit den meisten Übergängen in der Mitte  
-Oder in Kreisen (DONE)  
-Gesamt-distanz aller Übergänge möglicht klein  

#### nicht erreichbare Zustände entfernen (DONE)
Egal ob Automat Epsilon, NEA oder (min.) DEA ist  
S. 20 Skript


### Features
#### Info zum Automaten als Menueintrag -> Popup mit dem Info-Text und Scrollbar (DONE) (TODO: is minimal DEA)
-Angeben ob Automat ein Epsilon, NEA, DEA oder minimaler DEA ist  
-Def. Skript S. 10 angeben, also Zustände, Eingabealphabet, Überführungsfunktion, Startzustand, Endzustände angeben  

#### Epsilon Automat -> NEA (Menüeintrag to NEA) (DONE)
-S. 18 Skript  
-Epsilon -> NEA -> DEA -> minimaler DEA wäre die Hackordnung  

#### NEA -> DEA (Menüeintrag to DEA)
Skript 2.2.7: Aus jedem NEA kann man einen DEA konstruieren der die gleiche Sprache akzeptiert  
3 Bsps. Skript S. 13 / 14  
Nicht erreichbar Zustände entfernen ist schon implementiert, S. 17  
Falls ein Epsilon-Automat vorliegt, muss dieser natürlich zuerst in einen NEA transformiert werden  

#### DEA -> minimaler DEA (Menüeintrag to minimal DEA)
Sript S. 19  
Falls der Ausgangs-DEA nicht schon minimal ist, hat der neue DEA weniger Zustände (die minimale Anzahl) als der alte.  
Die beiden DEAs akzeptieren die gleiche Sprache.  

------------------------------------------------------------------------------------------------------
#### Regex -> Epsilon-Automat (als Menüeintrag)
-Den Beweis des Satzes 5.3 Sript S. 50. Müsste den Automaten nach der rekursiven Definition der regulären Ausdrücke von innen
heraus konstruieren. Könnte den Regex z.B. in einen binären Baum giessen und dann von den Ästen aus aufbauen. Braucht auf jeden
Fall eine 'merge'-Methode von zwei Automaten - sicherstellen dass Zustand-Indices eindeutig bleiben. (Skript S. 51)  
-Sript S. 9 eigene Notizen als ein Testbeispiel  

#### EA -> Regex ( als Menüeintrag). 
(Akzeptierte Sprache des Automaten berechnen. Genauer: Regex berechnen, der die akzeptierte Sprache des Automaten beschreibt.)  
-Ausgangsautomat ist ein EA (endliche Automaten sind (min.) DEA, NEA, Epsilon S. 75)  
-Beweis zu Satz 5.5 Sript S. 52  
-Den Regex (die akzeptierte Sprache A(E) ) auch noch als Menge aufschreiben? S. 13 Bsp. 2.1.7  
-Performance? Evtl. da ne kleine Performance-Analyse machen  

#### Aus Regex einen Minimalautomaten konstruieren.
-Regex nach Epsilon-Automat  
-Epsilon-Automat nach minimal DEA  

#### Wort akzeptiert? Skript S. 11 (Menüeintrag) (DONE)
-für alle Automat-Typen implementieren  
-Könnte ich implementieren genau wie schon in der rekursiven Util.markReachableNeighborStates(), einfach noch das Wort als param übergeben und 
bei der Transition nur aufrufen wenn auch das aktuelle Symbol enthalten ist, bei Epsilon-Übergang keins lesen. Falls ja, dieses Symbol vom Wort-String
abtrennen und mit übergeben. Die benutzten Transitions irgendwo speichern. Vorsicht bei Zyklen aus Epsilon-Übergängen, da terminiert mein Algorithmus nicht,
er liest kein Symbol und geht im Kreis rum. Die Zyklen vom Automaten entfernen geht aber auch nicht da dies die akzeptierte Sprache des Automaten veränden
kann. Also 'Epsilon-Zyklen' erkennen und bei diesen die rekursive Methode nicht aufrufen, sondern zur nächsten transition gehen.  

--- Alte Idee, verworfen ---  
-Gibts wahrscheinlich libraries die testen ob ein Wort Teil der erzeugten Sprache des Regex ist, somit ist die Strategie:  
-Automat -> Regex  
(-Regex umformatieren)  
-Testen ob das Wort 'Teil' des regulären Ausdruckes ist  

#### Visuell anzeigen wie Wort verarbeitet wird (DONE)
Welche Zustände, Symbole & Übergänge benutzt werden und diese aufleuchten lassen der Reihe nach. Oder ein kleines Bällchen über die 
Übergänge wandern lassen.  
-Ist wahrscheinlich praktisch wenn man vorher abfragen kann ob das Wort akzeptiert wird  
-Man könnte wieder rekursiv durch! Wie beim Labyrinth-Bsp am Ende von Programmieren 1. Also wenns nicht mehr weitergeht zurück zum Zustand wo man
eine andere Richtung einschlagen könnte (das gleiche Symbol mit einer anderen Transition lesen kann.
Beim DEA natürlich nicht möglich bzw. nötig da dies determiniert ist)  

#### NEA nach Typ-3 Grammatik, Satz 7.9 S. 74. Sript S. 75 (endliche Automaten, Regex und Typ-3 Grammatiken sind gleichmächtig). (DONE)
-Jeder (minimale) DEA ist auch ein NEA.  
-Epsilon-Automat nach NEA vor der Berechnung  
-endliche Automaten sind DEA, NEA, Epsilon S. 75  

-Notiz: Sript S. 27 Es gibt Sprachen, die nicht von endlichen Automaten erkannt werden. Also Regex kann nicht alle Sprachen darstellen.
z.b. a^nb^n geht nicht  
  

--- Ne Menge testen ob durch die Automaten-Umwandlungen und Regex <-> Automat die akzeptierte Sprache wirklich nicht verändert wurde. Und obs
durch das ganze undo / redo, save / load und lange Benutzung aller features des Editors wirklich nie kracht.