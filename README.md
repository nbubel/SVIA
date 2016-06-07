# SVIA
Single Video Individual Audio - EU-Project - ESPACE - RBB - NOTERIK

Die Abkürzung SVIA steht für "Single Video Individual Audio".
Im Kern wird die korrekte Synchronisation zwischen bliebig vieler Audioplayer auf mobilen Devices zu einem Videoplayer auf einem Mainscreen gezeigt.

Dies erfolgt mittels einem sogenannten Masterclock-Thread der als Taktgeber fungiert und beim Starten des Videos auf dem Mainscreen als Taktgeber die Masterclock startet.
Alle 5 Sekunden wird dann die aktuelle Zeit an alle aktiven Devices als JSON-Objekt über die Internetverbindung gesendet. Dort findet lokal per Javascript die eigentliche Synchronisation durch die Regulierung der Abspielgeschwindigkeit oder ggf. den Sprung an die korrekte Zeit statt.

Die Implementierung wurde mit dem Java-Framework "Springfield" des Projektpartners Noterik (Niederlande) umgesetzt.

Der Prototyp läuft auf einem LINUX-Server mit einer Tomcat-Umgebung.
