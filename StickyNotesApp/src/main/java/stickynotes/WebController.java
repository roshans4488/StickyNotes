package stickynotes;
import org.springframework.social.dropbox.connect.DropboxAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.social.dropbox.connect.DropboxConnectionFactory;
import org.springframework.social.dropbox.connect.DropboxServiceProvider;
import org.springframework.social.dropbox.api.Dropbox;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;

import com.dropbox.core.*;

import java.io.*;
import java.util.Locale;
@Controller
public class WebController extends WebMvcConfigurerAdapter  {
	//private OAuthToken accessToken;
	private HttpServletRequest request;
	private  HttpSession session;
	private String sessionKey;
	 private DbxSessionStore csrfTokenStore;
	 private DbxAppInfo appInfo;
	 private DbxRequestConfig config;
	 private  DbxWebAuth webAuth ;
	private String appKey="plquakrf1h9nx7t",appSecret="a0ohup3pzt86gxg",accessToken = "";
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String showForm(Person person) {
        return "form";
    }

    @RequestMapping(value="/", method=RequestMethod.POST)
    public String checkPersonInfo(@Valid Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form";
        }
        
        String authorizeUrl="" , authenticateUrl ="";
        try{
        
       /* DropboxConnectionFactory connection =  new DropboxConnectionFactory(appKey,appSecret,true);
        OAuth2Operations oauthOperations = connection.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri("http://localhost:8080/results");
         authorizeUrl = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
         authenticateUrl = oauthOperations.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT , params);
        System.out.println("authorizeUrl: " + authorizeUrl);
        System.out.println("authenticateUrl: " + authenticateUrl);
        */
        // connection =  new DropboxConnectionFactory(appKey,appSecret,authorizeUrl,authenticateUrl,true);
       //  oauthOperations = connection.getOAuthOperations();
       // OAuthToken accessToken = oauthOperations.exchangeForAccessToken(new AuthorizedRequestToken(requestToken, oauthVerifier), OAuth1Parameters.NONE);
       // DropboxServiceProvider dropBoxServiceProvider = new DropboxServiceProvider(oauthOperations,true); 
       // Dropbox dropBox = dropBoxServiceProvider.getApi(accessToken);
        
       // System.out.println("user profile:"+dropBox.getUserProfile());
       // dropBox.createFolder("Roshan");
        
        
        
        /*
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri("http://localhost:8080/results");
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
        System.out.println("authorizeUrl: " + authorizeUrl);
        "redirect:/"+authorizeUrl
        String accessToken = "AAAEziPz7ToEBAHsiLzF4GAte8oGa7lFTbzuRoZBzMubg2gfNSt0K2DS72sQSRmyRCVhRDjmmV4QWcFZCjTn6XPqsVIHltc1LpZA0kmJ2VxSYVw1TuwN";

        // upon receiving the callback from the provider:
        AccessGrant accessGrant = oauthOperations.exchangeForAccess(accessToken, "http://localhost:8080/JSPTest/index.htm", null);
        Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);
        return "home";
        */
        	
        	
        	
        	 
        	
        	ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            //return attr.getRequest().getSession(true);
        	
        	
        	 request = attr.getRequest() ;
             session = request.getSession(true);
            sessionKey = "dropbox-auth-csrf-token";
             csrfTokenStore = new DbxStandardSessionStore(session, sessionKey);
        	
        	
        	 appInfo = new DbxAppInfo(appKey, appSecret);
            //getAccessToken
             config = new DbxRequestConfig("StickyNotes", Locale.getDefault().toString());
            //DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
            
             webAuth = new DbxWebAuth(config, appInfo,"http://localhost:8080/results",csrfTokenStore);
            
            
     
        	//DbxAuthFinish authFinish = webAuth.finish(authorizationCode);
            
            authorizeUrl = webAuth.start();
             	
        	
        	
        	
        
        }
        
        catch(Exception e)
        {
        	
        	System.out.println("Exception"+e);
        }
        //return "redirect:/results";
        return "redirect:"+authorizeUrl;
    }
    
    
    @RequestMapping(value = "/results")
    public @ResponseBody String callback(@RequestParam(value = "code") String authorizationCode) throws IOException, DbxException {
      

    	  
    	 try {
    		 DbxAuthFinish authFinish = webAuth.finish(request.getParameterMap());
    		 accessToken =  authFinish.accessToken;
    	 }
    	catch(DbxWebAuth.BadRequestException ex)
    	 {
    		System.out.println(ex);
    		//return;
    	 }
    	 catch (DbxWebAuth.BadStateException ex) {
    	     // Send them back to the start of the auth flow.
    	     //response.sendRedirect("http://my-server.com/dropbox-auth-start");
    	     //return;
    		 System.out.println(ex);
    	 }
    	 catch (DbxWebAuth.CsrfException ex) {
    	     //log("On /dropbox-auth-finish: CSRF mismatch: " + ex.getMessage());
    	    // return;
    		 System.out.println(ex);
    	 }
    	 catch (DbxWebAuth.NotApprovedException ex) {
    	     // When Dropbox asked "Do you want to allow this app to access your
    	     // Dropbox account?", the user clicked "No".
    		 System.out.println(ex);
    	    // return;
    	 }
    	 catch (DbxWebAuth.ProviderException ex) {
    	     //log("On /dropbox-auth-finish: Auth failed: " + ex.getMessage());
    	     //response.sendError(503, "Error communicating with Dropbox.");
    	     //return;
    		 System.out.println(ex);
    	 }
    	 catch (DbxException ex) {
    	     //log("On /dropbox-auth-finish: Error getting token: " + ex.getMessage());
    	    // response.sendError(503, "Error communicating with Dropbox.");
    	     //return;
    		 System.out.println(ex);
    	 }
    	
    	 
  
    	 System.out.println("authorizationCode:"+authorizationCode);
    	 System.out.println("accessToken:"+accessToken);
    	 
    	//OAuthToken accessToken = oauthOperations.exchangeForAccessToken(new AuthorizedRequestToken(requestToken, oauthVerifier), OAuth2Parameters.NONE);
      
    	/*System.out.println("Authorization code:"+authorizationCode);
    	
    	ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        //return attr.getRequest().getSession(true);
    	
    	
    	HttpServletRequest request = attr.getRequest() ;
        HttpSession session = request.getSession(true);
        String sessionKey = "dropbox-auth-csrf-token";
        DbxSessionStore csrfTokenStore = new DbxStandardSessionStore(session, sessionKey);
    	
    	
    	DbxAppInfo appInfo = new DbxAppInfo(appKey, appSecret);
        //getAccessToken
        DbxRequestConfig config = new DbxRequestConfig("StickyNotes", Locale.getDefault().toString());
        //DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
        
        DbxWebAuth webAuth = new DbxWebAuth(config, appInfo,"http://localhost:8080/results",csrfTokenStore);
        
        
 
    	//DbxAuthFinish authFinish = webAuth.finish(authorizationCode);
        
        String authorizePageUrl = webAuth.start();
        */
        
        
        /*
        try {
        	DbxAuthFinish authFinish = webAuth.finish(request.getParameterMap());
        }
        catch (DbxWebAuth.BadRequestException ex) {
            System.out.println(ex);
        }
        
        
        accessToken = authFinish.*/
    	//DbxAuthFinish authFinish = webAuth.finish(authorizationCode);
        //accessToken = authFinish.accessToken;

       
        
        DbxClient client = new DbxClient(config, accessToken);
        System.out.println("Linked account: " + client.getAccountInfo().displayName);
        
        File inputFile = new File("C:/java8book-HerbertSchildt.pdf");
        FileInputStream inputStream = new FileInputStream(inputFile);
        try {
            DbxEntry.File uploadedFile = client.uploadFile("/java8book.pdf",
                DbxWriteMode.add(), inputFile.length(), inputStream);
            System.out.println("Uploaded: " + uploadedFile.toString());
        } finally {
            inputStream.close();
        }

        DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
        System.out.println("Files in the root path:");
        for (DbxEntry child : listing.children) {
            System.out.println("	" + child.name + ": " + child.toString());
        }

        FileOutputStream outputStream = new FileOutputStream("java8book.pdf");
        try {
            DbxEntry.File downloadedFile = client.getFile("/java8book.pdf", null,
                outputStream);
            System.out.println("Metadata: " + downloadedFile.toString());
        } finally {
            outputStream.close();
        }
    	
    	
    	return "File uploaded!";
    }
    
    
    
    

}
