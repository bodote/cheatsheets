Ein großes Unternehmen das bis vor wenigen Jahren hauptsächlich ca 300 verschiedene Softwareprodukte als on-premise installierbare Windowsprogramme in C# geschreiben entwickelt hat, hat nun angefangen Nachfolgeprodukte zu entwickeln die als Webanwendungen in der Cloud laufen sollen. 

Um den Anwendern zu erleichtern von den alten auf die neuen Produkte umzusteigen, soll es eine eigene Cloudanwendung geben, die den Anwender durch die Umstellung leitet: Diese Anwendung wird die folgenden Funktionen bieten: 
1. Benutzerfreundliche Schulungen und Tutorials
2. Unterstützung bei der Datenmigration
3. Integration mit bestehenden Systemen

Diese Anwendung heißt "MigrationsHelfer".

Nun wurde für genau eines der 300 Produkte, für die "MyApplicationA", bereits ein solcher "MigrationsHelfer" implementiert, der allerdings eine spezielle Anpassung für "MyApplicationA" enthält. Umgekehrt fehlt jede Art von verallgemeinerung auf die anderen 299 Produkte.

Nun stellt sich die Softwarearchitekturfrage ob es besser ist, eine komplett neue implementierung des "MigrationsHelfer" zu schreiben, die dann für alle 300 Produkte funktioniert, oder ob es besser ist, den bestehenden "MigrationsHelfer" incrementell zu modifizieren, dass er für alle 300 Produkte funktioniert.

Zusätzliche Komplikation: der bisherige MigrationsHelfer ist bereits im produktiven Einsatz und wird von den Anwendern gut angenommen. Jede incrementelle Änderung muss also so erfolgen, dass die Anwender nicht gestört werden.

Was würden Sie empfehlen?




