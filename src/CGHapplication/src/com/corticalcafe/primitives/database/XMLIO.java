package com.corticalcafe.primitives.database;

/**
 * <p>Title: DataLogger</p>
 * <p>Description: Generic Data Logging Package</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Alan Stein
 * @version 1.0
 */

import com.corticalcafe.primitives.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.xpath.*;

import java.io.*;
import java.net.*;

/**
 * DB IO implementation which simply reads/writes flat files or passes XML info via a webDBserver page which is
 * running in JSP on the host.  As such, the data (a collection)
 * used can only be a rectangular dataset and is implemented as a Vector.
 *
 * Simplest method to read/write file XML is:
 *   xmlIO=new XMLIO();
 *   xmlIO.writeXML(fileName, XMLIO.parseToDocument(tableElements));
 *
 * <p>Title: DataLogger</p>
 * <p>Description: Generic Data Logging Package</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Alan Stein
 * @version 1.0
 */

public class XMLIO
{
  private static final int DB_UNDEFINED= -1;
  private static final int DB_FILE=0;
  private static final int DB_WEB=1;

  private static boolean createNewDirectories=true;

  private static int dbType=DB_UNDEFINED; //change to STATIC 5/27/04, this will BREAK THINGS if we want both a WEB and FILE db at same time
  String baseDBfilePath="";
  URL baseDBURL;
  String dbName="";

  public XMLIO()
  {
  }

  /**
   * this construct to specify a DB that is file based
   * @param baseDBfilePath
   * @param dbName
   */
  public XMLIO(String baseDBfilePath, String dbName)
  {
    setDBbase(baseDBfilePath, dbName);
  }

  /**
   * this construct to specify a DB that is URL based (ie, has a webDBserver running
   * on JSP at the hosting end)
   * @param baseURL
   * @param dbName
   */
  public XMLIO(URL baseURL, String dbName)
  {
    setDBbase(baseURL, dbName);
  }

  public void setDBbase(String baseDBfilePath, String dbName)
  {
      this.baseDBfilePath=baseDBfilePath;
      this.dbName=dbName;
      dbType=DB_FILE;
  }

  public void setDBbase(URL baseURL, String dbName)
  {
      this.baseDBURL=baseURL;
      this.dbName=dbName;
      dbType=DB_WEB;
  }

  public Document readXML(String tableName)
  {
    if(dbType==DB_FILE)
      return readXMLfile(tableName);
    else
    if(dbType==DB_WEB)
      return readXMLwebDB(tableName);
    else
      return null;
  }


  /**
   * read an XML document.  If you are using the full database capability of this
   * class, then use the readXML() method which allows web/file independent XML access.
   * @param f file to read
   * @return JDOM document
   */
  static public Document readXMLfile(File f)
  {
    try
    {
      Document retDoc;

      if(!f.exists())
      {
        DebugMessages.error("File "+f.getCanonicalPath()+" doesn't exist...");
        return null;
      }

      FileReader fr=new FileReader(f);
      BufferedReader br=new BufferedReader(fr);
      retDoc=readXML(br);
      br.close();
      return retDoc;
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    catch (JDOMException ex)
    {
      ex.printStackTrace();
    }

    return null;
  }


  /**
   * read XML file into JDOM Document, shouldn't be called directly
   * @param docName - name of file
   * @return
   */
  private Document readXMLfile(String tableName)
  {
    tableName=tableName.concat(".xml");

//    DebugMessages.debug("Reading XML File "+tableName);
    File f=new File(baseDBfilePath+File.separator+dbName+File.separator+tableName);

    return readXMLfile(f);
  }


  /**
   * read XML file into JDOM Document
   * @param docName - name of file
   * @return
   */
  public static Document parseToDocument(String xmlStr)
  {
//    DebugMessages.debug("Tring to parse XML string:  \""+xmlStr+"\"");

    if(xmlStr==null)
      return null;

    Document retDoc;

//    DebugMessages.debug(this.getClass().getName()+":  reading XML String to Document");
    try
    {
      CharArrayReader car=new CharArrayReader(xmlStr.toCharArray());
      BufferedReader br=new BufferedReader(car);
      retDoc=readXML(br);
      br.close();
      return retDoc;
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    catch (JDOMException ex)
    {
      ex.printStackTrace();
    }

    return null;
  }


  /**
   * write XML to String from JDOM Document
   * @param docData - data
   * @return
   */
  public static String parseFromDocument(Document docData)
  {
    if(docData==null)
      return null;

//    DebugMessages.debug(this.getClass().getName()+":  writing XML to String");
    String retStr;
    CharArrayWriter caw=new CharArrayWriter();

    try
    {
      BufferedWriter bw=new BufferedWriter(caw);
      writeXML(bw, docData);
      bw.close();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }

    retStr=caw.toString();
    return retStr;
  }


  public void writeXML(String tableName, Element rootElem)
  {
    writeXML(tableName, new Document(rootElem));
  }


  public void writeXML(String tableName, Document docData)
  {
    if(dbType==DB_FILE)
      writeXMLfile(tableName, docData);
    else
    if(dbType==DB_WEB)
      writeXMLwebDB(tableName, docData);
  }


  public void deleteXML(String tableName)
  {
    if(dbType==DB_FILE)
      deleteXMLfile(tableName);
    else
    if(dbType==DB_WEB)
      deleteXMLwebDB(tableName);
  }


  private void deleteXMLfile(String tableName)
  {
    tableName=tableName.concat(".xml");

//    DebugMessages.debug("Deleting file "+tableName);

    try
    {
      Document retDoc;
      File f=new File(baseDBfilePath+File.separator+dbName+File.separator+tableName);

      if(!f.exists())
      {
        DebugMessages.error("File "+f.getCanonicalPath()+" doesn't exist...");
      }

      f.delete();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * write an XML document. If you are using the full database capability of this
   * class, then use the writeXML() method which allows web/file independent XML access.
   * @param f
   * @param docData
   */
  static public void writeXMLfile(File f, Document docData)
  {
    try
    {
      if(dbType==DB_FILE)   //if using in DB mode with full tree structure, check validity of DB
      {
        if(!f.getParentFile().getParentFile().exists()) //check for existence of DB directory
        {
          DebugMessages.error("FileWrite error:  ("+f.getParentFile().getParent()+") doesn't exist");
          return;
        }

        if(createNewDirectories && !f.getParentFile().exists()) //create DB subdirectory if necessary and allowed
        {
          File tmpF=new File(f.getParent());
          tmpF.mkdir();
//        DebugMessages.debug("created new DBname");
        }
      }

//      DebugMessages.debug("Trying to write "+f.getPath());

      FileWriter fr=new FileWriter(f);
      BufferedWriter bw=new BufferedWriter(fr);
      writeXML(bw, docData);
      bw.close();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }

  }

   /**
   * write XML file from JDOM Document
   * @param docName - name of file
   * @param docData - data
   * @return
   */
  private void writeXMLfile(String tableName, Document docData)
  {
    tableName=tableName.concat(".xml");

//    DebugMessages.debug("Writing XML to File "+tableName);
    File f=new File(baseDBfilePath+File.separator+dbName+File.separator+tableName);

    writeXMLfile(f, docData);
  }

  private String sendXMLwebDBcommand(String tableName, Document docData, String action)
  {
    String retStr=null;

//    DebugMessages.debug("WebCommand:  Table= "+tableName+" Action="+action);

    Document retVal=null;

    String protocol = baseDBURL.getProtocol();
    String host = baseDBURL.getHost();
    String portString = ""+baseDBURL.getPort();
    int port;
    try
    {
      port = Integer.parseInt(portString);
    }
    catch(NumberFormatException nfe)
    {
      port = -1; // I.e., default port of 80
    }

    String path=baseDBURL.getPath();

    try
    {
/*    We could manually build HTTP-POST header and data and write to socket
      OutputStream out = null; DataInputStream in = null;
      Socket s = null;

      String postData = "key1=value&key2=value";
        try {
            s = new Socket(server, port);
            out = s.getOutputStream();

        // --- write your own HTTP-POST Header ----
            String header = "POST " + servlet + " HTTP/1.0\n"
                + "Content-type: application/x-www-form-urlencoded\n"
                + "Content-length: " + postData.length()
                + "\n\n";
            out.write(header.getBytes());
            out.write(postData.getBytes());
            out.flush();

        // read answer if any
            in = new DataInputStream(new
                 BufferedInputStream(s.getInputStream()));

or we can use the HttpURLConnection class and let it set things up for us...
 */

      URL url = new URL(protocol, host, port, path);
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      http.setDoInput(true);
      http.setDoOutput(true);
      http.setUseCaches(false);
      http.setRequestMethod("POST");
      String postData;

      if(docData!=null)
        postData =
          "dbName=" + URLEncoder.encode(dbName, "UTF-8") +
          "&tableName=" + URLEncoder.encode(tableName, "UTF-8") +
          "&action=" + URLEncoder.encode(action, "UTF-8") +
          "&tableData=" + URLEncoder.encode(parseFromDocument(docData), "UTF-8");
      else
        postData =
          "dbName=" + URLEncoder.encode(dbName, "UTF-8") +
          "&tableName=" + URLEncoder.encode(tableName, "UTF-8") +
          "&action=" + URLEncoder.encode(action, "UTF-8") +
          "&tableData=" + URLEncoder.encode("<data>no data present</data>", "UTF-8");

      http.setRequestProperty("Content=length", String.valueOf(postData.getBytes().length));

      http.connect();
//      DebugMessages.debug("Client : Connected");

      DataOutputStream out = new DataOutputStream(http.getOutputStream());

//      DebugMessages.debug("Client : Writing Content");
      out.writeBytes(postData);

//      DebugMessages.debug("Client : Flushing Stream");
      out.flush();

//      DebugMessages.debug("Client : Waiting for response from Server");

      BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
//      DebugMessages.debug("Client : Opened input stream");

      String input = "";
      StringBuffer response = new StringBuffer();
      while((input = in.readLine()) != null)
      {
        response.append(input+"\r");
      }
//      DebugMessages.debug("Client : received : "+response);

      retStr=response.toString();

      http.disconnect();
    }
    catch(Exception e)
    {
      DebugMessages.error("Client : Error : "+e.getMessage());
    }

    return retStr;
  }

  /**
   * read a table via the webServer.jsp page
   * Assumes baseDBURL and baseDBfilePath have already been set up
   * @param tableName - name of table
   * @return XML document table
   */
  private Document readXMLwebDB(String tableName)
  {
//    DebugMessages.debug("WebReadDB "+tableName);

    Document retVal=null;

    String response=sendXMLwebDBcommand(tableName, null, "query");

    String tableDataResponse=getDataResponse("tableData", response);

//    DebugMessages.debug("dbName="+getDataResponse("dbName", response));
//    DebugMessages.debug("tableName="+getDataResponse("tableName", response));
//    DebugMessages.debug("tableData="+tableDataResponse);

    if(tableDataResponse!=null && tableDataResponse.compareTo("null")!=0 && tableDataResponse.compareTo("")!=0)
      retVal=parseToDocument(getDataResponse("tableData", response));

    return retVal;
  }


  /**
   * parses a parameter from the webServer HTML response where it was coded into a machine-readable comment
   * of the form<br/><pre>
   *
   * <!--BEGIN paramName RESPONSE
   *    ...data goes here...
   * END paramName RESPONSE-->
   * </pre>
   *
   * @param paramName - parameter to parse (eg, "tableName")
   * @param htmlReponseStr - string to parse from
   * @return - parameter value
   */
  private static String getDataResponse(String paramName, String htmlReponseStr)
  {
    String retStr=null;

    String str0="<!--BEGIN "+paramName+" RESPONSE";
    String str1="END "+paramName+" RESPONSE-->";
    int idx0=htmlReponseStr.indexOf(str0)+str0.length();
    int idx1=htmlReponseStr.indexOf(str1);

    if(idx0>0 && idx1>0 && idx1>idx0)
      retStr=htmlReponseStr.substring(idx0, idx1);

    return retStr.trim();
  }


  /**
   * write a table to the DB via the webServer.jsp mechanism
   * @param tableName - table name to write
   * @param docData - XML document to be written
   */
  private void writeXMLwebDB(String tableName, Document docData)
  {
//    DebugMessages.debug("WebWriteDB "+tableName);

    sendXMLwebDBcommand(tableName, docData, "post");
  }



    /**
     * delete a table from the DB via the webServer.jsp mechanism
     * @param tableName - table name to delete
     */
    private void deleteXMLwebDB(String tableName)
    {
//      DebugMessages.debug("WebDeleteDB");

      sendXMLwebDBcommand(tableName, null, "delete");
  }



  private Properties getTokenizedQuery(String query)
  {
    Properties params = new Properties();
    StringTokenizer st = new StringTokenizer(query,"&/");
    while (st.hasMoreTokens())
    {
        String s = st.nextToken();
        int i = s.indexOf("=");
        if (i > 0)
            try
            {
                params.setProperty(s.substring(0,i),URLDecoder.decode(s.substring(i+1),"UTF-8"));
            } catch (Exception e) {}
    }

    return params;
  }



  /**
   * read XML document from input stream
   * @param in - Input Stream
   * @return
   * @throws IOException
   * @throws JDOMException
   */
  private static synchronized Document readXML(BufferedReader in) throws IOException, JDOMException
  {
//    DebugMessages.debug(this.getClass().getName()+":  reading XML from BufferedReader");
    Document doc=new Document();
    SAXBuilder sb=new SAXBuilder();
    doc=sb.build(in);
    return doc;
  }

  /**
   * write XML document to output stream
   * @param out - Output Stream
   * @param doc - XML document
   * @throws IOException
   */
  private static synchronized void writeXML(BufferedWriter out, Document doc) throws IOException
  {
//    DebugMessages.debug(this.getClass().getName()+":  writing XML to BufferedWriter");
    XMLOutputter xo=new XMLOutputter(Format.getPrettyFormat());
//    XMLOutputter xo=new XMLOutputter("  ", true);
//    XMLOutputter xo=new XMLOutputter("  ", false);
    xo.output(doc, out);
  }

  /** Given a string, this method replaces all occurrences of
   *  '<' with '&lt;', all occurrences of '>' with
   *  '&gt;', and (to handle cases that occur inside attribute
   *  values), all occurrences of double quotes with
   *  '&quot;' and all occurrences of '&' with '&amp;'.
   *  Without such filtering, an arbitrary string
   *  could not safely be inserted in a Web page.
   */
  public static String filterEntities(String input) {
    StringBuffer filtered = new StringBuffer(input.length());
    char c;
    for(int i=0; i<input.length(); i++) {
      c = input.charAt(i);
      if (c == '<') {
        filtered.append("&lt;");
      } else if (c == '>') {
        filtered.append("&gt;");
      } else if (c == '"') {
        filtered.append("&quot;");
      } else if (c == '&') {
        filtered.append("&amp;");
      } else {
        filtered.append(c);
      }
    }
    return(filtered.toString());
  }


  public static Document toDocumentFromRoot(Element rootElem)
  {
    Document retDoc=new Document(rootElem);
    return retDoc;
  }

  public static Element getRootfromDocument(Document doc)
  {
    Element rootElem=doc.getRootElement();
    return rootElem;
  }
  }
