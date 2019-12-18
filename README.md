<h3>1 Serverstart</h3>

Der Server lässt sich einfach mit dem Befehl <pre>java -jar simplewebserver.jar</pre> starten. Um die Vorgabewerte anzupassen, können die im Folgenden aufgeführten Kommandozeilenparameter angegeben werden. Alle Parameter sind optional.

<table class="table table-condensed">
            <tr>
                <th>Parameter</th>
                <th>Name</th>
                <th>Datentyp</th>
                <th>Default</th>
                <th>Beschreibung</th>
            </tr>
            <tr>
                <td>p</td>
                <td>Serverport</td>
                <td>integer</td>
                <td>8080</td>
                <td>Port für eingehende Verbindungen</td>
            </tr>
            <tr>
                <td>d</td>
                <td>Document Root</td>
                <td>string</td>
                <td>/var/www</td>
                <td>Basisverzeichnis für Dateianfragen</td>
            </tr>
            <tr>
                <td>t</td>
                <td>Threads pro Core</td>
                <td>integer</td>
                <td>25</td>
                <td>Maximale Anzahl von Threads pro Core</td>
            </tr>
</table>
Beispiel: <code>java -jar simplewebserver.jar -p 4242 -d /usr/share/doc</code>
        
<h3>2 Abhängigkeiten</h3>

<ul>
            <li>Apache <code>log4j2</code> (Logging)</li>
            <li>Apache <code>commons-cli</code> (Command Line Utility) und</li>
            <li>Apache <code>httpcore</code> (Low-Level HTTP API)</li>
</ul>
        
        
<h3>3 Implementierung</h3>

Einsprungpunkt der Anwendung ist die Klasse <em>Main</em>. Sie führt das Parsing der Kommandozeilenparameter durch und startet den <em>SimpleWebserver</em>.

Die Aufgabe der Klasse <em>SimpleWebserver</em> ist es, auf einkommende Verbindungen zu lauschen und mittels eines Threadpools einen neuen <em>HttpRequestListener</em>-Thread zu starten, sobald eine neue Verbindung hergestellt wurde.

Der <em>HttpRequestListener</em> ist für die Verwaltung von genau einer HTTP-Verbindung zuständig und verarbeitet alle über diese Verbindung eingehenden HTTP-Requests. Die Verwaltung umfasst das Management persistenter Verbindungen und das Auslösen nachgelagerter Handler, welche die empfangenen Requests verarbeiten.

Sobald ein neuer Request eintrifft, wird dieser vom <em>HttpFileHandler</em> entgegengenommen und an die ResponseHandler-Chain weitergeleitet. Die beinhalteten <em>ResponseHandler</em> wurden mit dem Ziel entworfen, unkompliziert ein- und aushängbar zu sein, also unter Vermeidung des Umstands, unnatürliche Abhängigkeiten zwischen den Komponenten zu schaffen. Wird ein neuer Request verarbeitet, entscheidet ein aufgerufener Handler, ob er für die Verarbeitung zuständig ist oder den Request weitergibt. Die Chain besteht aus den folgenden Handlern:

<ul>
            <li><em>FileNotFoundHandler</em></li>
            <li><em>DirectoryListingHandler</em></li>
            <li><em>ServeFileHandler</em></li>
            <li><em>IfMatchHandler</em></li>
            <li><em>IfNoneMatchHandler</em></li>
            <li><em>IfModifiedSinceHandler</em></li>
</ul>

Um Codeduplizierung zu vermeiden, sind <em>IfMatchHandler</em> und <em>IfNoneMatchHandler</em> von der Klasse <em>AMatchHandler</em> abgeleitet, welche die Matchinglogik beinhaltet.

Die Klassen <em>HashUtil</em> und <em>DateUtil</em> stellen Utility-Klassen dar, welche statische Helfermethoden zur Hashwertberechnung von Dateien bzw. zum Parsen und Erzeugen von HTTP-Dates zur Verfügung stellen.
        
<h3>Referenzen</h3>
        <ul>
            <li><b>RFC 2616</b> &ndash; Hypertext Transfer Protocol -- HTTP/1.1</li>
            <li><b>RFC 7232</b> &ndash; Hypertext Transfer Protocol (HTTP/1.1): Conditional Requests</li>
            <li><b>Tutorial HttpCore 4.4.5</b> &ndash; <a href="https://hc.apache.org/httpcomponents-core-4.4.x/tutorial/html/index.html">https://hc.apache.org/httpcomponents-core-4.4.x/tutorial/html/index.html</a></li>
        </ul>
