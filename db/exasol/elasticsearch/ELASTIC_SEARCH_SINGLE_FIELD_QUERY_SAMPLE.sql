CREATE OR REPLACE JAVA SCALAR SCRIPT ELASTIC_SEARCH_QUERY_SAMPLE ("searchParam" VARCHAR(255) UTF8) RETURNS VARCHAR(100000) UTF8 AS
/*

Creates and ElasticSearch Post Query 
queries a single field 
and returns an Entry from defined doucments

TODO: Adjust parameters and datatypes for your query

*/
	import java.sql.SQLException;
	import java.nio.charset.Charset;
	import java.io.BufferedReader;
	import java.io.StringWriter;
	import java.io.PrintWriter;
	import java.io.InputStreamReader;
	import java.io.Reader;
	import java.net.HttpURLConnection;
	import java.net.URL;
	import java.nio.charset.StandardCharsets;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	import java.util.Arrays;
	
	class ELASTIC_SEARCH_SINGLE_FIELD_QUERY_SAMPLE {
		/*	ElasticSearch URL settings
			TODO: adjust for your query
		*/
		static final String elasticHost 			= "192.168.0.99:9200";
		static final String elasticIndex 			= "anyIndex";
		static final String elasticDoc 				= "anyDocument";
		static final String requestUrlStr			= "http://" + elasticHost + "/" + elasticIndex + "/" + elasticDoc + "/_search";

		static final String elasticDocSearchField 	= "search_string";

		/* 	Regex to extract ID out of Elastic Search HTTPResponse  
			INFO: In my case the ID is a decimal
			TODO: Adjust for your query
		*/
		static Pattern patternIDString = Pattern.compile("(\"_id\":\"){1}([0-9]*){0,1}(\")");
		static Pattern patternExtractID = Pattern.compile("(\\d++)");
	
		/* Init JVM Instance shared HTTPConnection */
		static HttpURLConnection conn;
		static URL url;
		/* Execute once an each cluster node */
		static void init(final ExaMetadata exa) throws Exception{
			try {
				url = new URL(requestUrlStr);
				conn = (HttpURLConnection) url.openConnection();
				conn.disconnect();
			} catch (Exception e) {
				/* unhandled */
			} 
		}
		
		
		/* cleanUp not used but for your information that it is possible
		static void cleanup(final ExaMetadata exa) throws Exception{
			try {
				conn.disconnect();
			} catch (Exception e) {
			
			} 
		}
		*/
		
		/* Actual HTTP Request function */
		static String doHttpPostRequest(final String requestData) throws Exception {
		
			byte[] postData = requestData.getBytes( StandardCharsets.UTF_8 );
			int postDataLength = postData.length;

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput( true );
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
			conn.setUseCaches( false );

			conn.getOutputStream().write(postData);

			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();

			for (int c; (c = in.read()) >= 0;)
				sb.append((char)c);

			String response = sb.toString();
			in.close();
			conn.disconnect();

			Matcher m = patternIDString.matcher(response);
			if(m.find()){
				/*
					TODO: Adjust for your query
					This code is for my usecase (id is int)
				*/
				Matcher m2 = patternExtractID.matcher(m.group(0));
				if(m2.find()){
					return m2.group(1);
				}
			} 
			return "0";
		}
		
		/* Executed for each row */
		static String run(final ExaMetadata exa, final ExaIterator ctx) throws Exception {
			
			/* 	TODO: Adjust ElasticSearch json search query if needed
				see: https://www.elastic.co/guide/en/elasticsearch/reference/current/search.html
			*/
			String requestData = "{\"size\":1,\"query\":{\"match\":{\"" + elasticDocSearchField + "\":\"" + ctx.getString("searchParam") + "\"}}}";


			try {
				String requestResult = doHttpPostRequest(requestData);	
				return requestResult;	
			}
			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				
				return "-3";
				/*return errors.toString() + " \r\n---\r\n POST DATA \r\n " + requestData;*/
			} 
		}
	
	
	
	}