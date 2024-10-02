[Programm](https://archdoc.bettercode.eu/#programm)
# Architecture Inception oder Communication Canvas 
* https://canvas.arc42.org/#:~:text=Based%20upon%20ideas%20from%20the%20famous%20Business%20Model
* auch draw.io , powerpoint und convanizer.com für Templates zum ACC 
https://canvas.arc42.org/downloads#architecture-communication-canvas-acc
* Eingedampfte Version von Arch42

# C4 Docu
* UML zu unbekannt, wird kaum verwendet.
* zumindest einheitlichen Abstraktion 
* Bild über Text 
* verschiedenen Sichten für verschiedenen Zielgruppen.
* Einheitliches Vokabular
* Weniger ist mehr
* C4 : 
    * System **C**ontext
    * **C**ontainer
    * **C**omponent
    * ~~Code~~
* Diagram as code: 2.0 
    * Architektur in DSL beschreiben
    * Structurizr online oder in pipeline structurizr-site-generator 
```
structurizr-site-generatr --help
Usage: structurizr-site-generatr options_list
Subcommands: 
    serve - Start a development server
    generate-site - Generate a site for the selected workspace.
    version - Print version information 
Options: 
    --help, -h -> Usage info
``` 
* wer Markdown über MS-Word vorzieht, der wird auch Structurizr über draw.io vorziehen.

# DDD
* ddd-canvases: [github.com/ddd-crew](https://github.com/ddd-crew)
* Business Model Canvas ist wichtig für die Entwickler um das Problem und die Zielgruppe zu verstehen.
* Eventstorming erklärt
    * ES führt direkt zu Arc42 oder C4 Diagrammen "Context Sicht"
    * Bounded Context , Allgegenwärtige Sprache (KEINE schlechten Übersetztungen ins englische nutzen!)-> Bausteinsicht arc42, oder C4 Containerdiagram
    * Bounded Context Canvas
    * Domain Message Flow Modell -> arc42->Laufzeitsicht , dynamic diagram in C4
    * Quality Storming- -> Qualitätsanforderungen
* Zentraler Punkt: alles collaborativ im Workshopformat erstellen

# technische Redakteurin
* Fokus für Vortrag: externe öffentliche Docu 
* Unternehmensziele ?
* für wen überhaupt ?

# C Heitzmann. Wie gelingt ausgezeichnete Softwaredokumentation?
* docs as code
* empfehlung Docu wie Quellcode behandel 
* AsciiDoc, kein Markdown !, oder reStructuredText
* problem mit confluence spricht mir aus der seele
* KEIN markdown/asciidoc to Confluence - export empfohlen  
* Gegner von JederKannMitmachen
* docusystem Antora (statt Confluence)
* https://doctoolchain.org/docToolchain/v2.0.x/020_tutorial/070_publishToConfluence.html
* oder pandoc
* mvn plugin für exportieren ?

# Alexander Schwarz: Onlinedocu die hilft
* code as Docu 
* im git verwalten
* automatischer build
* AsciiDoc als DER standard empfohlen
* Kata zum Selbststudium !
* Intellij Plugin für asciidoc
* asciidoc validieren mit `.vale.ini` oder Grazie (Intellij Plugin)
* asciidoctor web site
* https://antora.org/
