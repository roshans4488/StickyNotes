package stickynotes;
import org.springframework.social.dropbox.connect.DropboxAdapter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	private HttpServletResponse response;
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
        	
        	ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
           
        	
        	
        	 request = attr.getRequest() ;
        	 System.out.println("request : " +request);
             session = request.getSession(true);
            sessionKey = "dropbox-auth-csrf-token";
             csrfTokenStore = new DbxStandardSessionStore(session, sessionKey);
        	
        	
        	 appInfo = new DbxAppInfo(appKey, appSecret);
          
             config = new DbxRequestConfig("StickyNotes", Locale.getDefault().toString());
           
             webAuth = new DbxWebAuth(config, appInfo,"http://localhost:8080/results",csrfTokenStore);
             System.out.println("webAuth:" +webAuth);
          
            authorizeUrl = webAuth.start();        
        }
        
        catch(Exception e)
        {
        	
        	System.out.println("Exception"+e);
        }
        
       
        System.out.println("authorizeUrl:" +authorizeUrl);
        /*
     // Set response content type
        response.setContentType("text/html");

        // New location to be redirected
        String site = new String(authorizeUrl);

        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", site); 
       //return "results";
        
        */
       return "redirect:"+authorizeUrl;
    }
    
    
    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public String callback(@RequestParam(value = "code") String authorizationCode) throws IOException, DbxException {
      
    	
    	try {
    		 DbxAuthFinish authFinish = webAuth.finish(request.getParameterMap());
    		 accessToken =  authFinish.accessToken;
    	 }
    	catch(DbxWebAuth.BadRequestException ex)
    	 {
    		System.out.println(ex);
    		
    	 }
    	 catch (DbxWebAuth.BadStateException ex) {

    		 System.out.println(ex);
    	 }
    	 catch (DbxWebAuth.CsrfException ex) {

    		 System.out.println(ex);
    	 }
    	 catch (DbxWebAuth.NotApprovedException ex) {

    		 System.out.println(ex);
 
    	 }
    	 catch (DbxWebAuth.ProviderException ex) {
 
    		 System.out.println(ex);
    	 }
    	 catch (DbxException ex) {

    		 System.out.println(ex);
    	 }
    	
    	 // write the code to save the access token in the database
  
    	 System.out.println("authorizationCode:"+authorizationCode);
    	 System.out.println("accessToken:"+accessToken);
    	 
    	
        DbxClient client = new DbxClient(config, accessToken);
        System.out.println("Linked account: " + client.getAccountInfo().displayName);
        
        /*
        File inputFile = new File("E:/Test.txt");
        FileInputStream inputStream = new FileInputStream(inputFile);
        try {
            DbxEntry.File uploadedFile = client.uploadFile("/java8book.txt",
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

        FileOutputStream outputStream = new FileOutputStream("java8book.txt");
        try {
            DbxEntry.File downloadedFile = client.getFile("/java8book.txt", null,
                outputStream);
            System.out.println("Metadata: " + downloadedFile.toString());
        } finally {
            outputStream.close();
        }
    	*/
    	
       return "redirect:Sticky";
    	 
    }
    
    @RequestMapping(value="/Sticky", method=RequestMethod.GET)
    public String redirect() {
       
        return "stickynote";
    }
}
