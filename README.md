# JasperServer_Client
A Java client source code to interact with Tibco JasperServer through his Rest API. Note that this was made for an Android application, so there could be things referring to the Android environment. It uses only standard Java classes, so there will not be the need to import any library in your project. Before usage you should change the base_url var that contains the URL at which to contact the server, usually, the format is "http(s)://xxx.xxx.xxx.xxx:8080/jasperserver/".
# Implemented functions
<div id="list">It contains a few useful functions:
<ul>
  <li><a href="#login">login</a></li>
  <li><a href="#directory">createDirectory</a></li>
  <li><a href="#rep">generateReport</a></li>
  <li><a href="#res">getResources</a></li>
  <li><a href="#upd">updateXml</a></li>
  <li><a href="#logout">logout</a></li>
</ul>
</div>
<hr>
<div id="login">
<h1>Login</h1>
The login function is used to log into the server. It requires the username and the password to use and returns the sessionId, a string that needs to be used in all future requests to identify a certain user. Can throw an IOException in case the connection can't be opened.</div>
<hr>
<div id="directory">
<h1>Create directory</h1>
A function to create a directory on the server. It requires the Uri of the new folder and the sessionId of the user who requires this action, it returns a boolean representing the outcome of the operation.
</div>
<hr>
<div id="rep">
<h1>Generate report</h1>
A function used to ask the server for the generation of a report. It also saves it in the default Android download folder as a pdf file. It requires the URI of the report to generate, the name to use to save the report, and the sessionId, it returns a boolean representing the outcome of the operation.
</div>
<hr>
<div id="res">
<h1>Get resources</h1>
A function used to query the server. It requires a Map in which to put all the parameters to refine the research and the sessionId, it returns a SearchResult, an object containing the response code received from the server, and eventually a list containing a representation of the elements found.
</div>
<hr>
<div id="upd">
<h1>Update xml</h1>
A function used to overwrite an existing data source, making it reference a new XML file that a report should use. It requires the URI of the new XML to use and the sessionId, it returns an integer that is the response code got from the server.
</div>
<hr>
<div id="logout">
<h1>Logout</h1>
A logout function to end the session serverside. It requires the sessionId and returns a boolean representing the outcome of the operation.
</div>
