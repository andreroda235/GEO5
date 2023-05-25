package pt.unl.fct.di.apdc.geo5.util;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@SuppressWarnings("serial")
public class MediaResourceServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private final String BUCKET = "apdc-geoproj.appspot.com";
	
	
	public MediaResourceServlet() {
		
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp, String filepath) throws IOException {
		//Upload to  Google Cloud Storage
		Storage storage = StorageOptions.getDefaultInstance().getService();
		BlobId blobId = BlobId.of(BUCKET, filepath);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(req.getContentType()).build();
		//The Following is deprecated since it is better to upload directly to GCS from the client
	    @SuppressWarnings({ "deprecation", "unused" })
		Blob blob = storage.create(blobInfo, req.getInputStream());
	    storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp, String filepath) throws IOException {
		//Download file from a specified bucket.
		Storage storage = StorageOptions.getDefaultInstance().getService();	
		Blob blob = storage.get(BlobId.of(BUCKET, filepath));
		//Download object to the output stream.
		ServletOutputStream output = resp.getOutputStream();
	    blob.downloadTo(output);
	    output.close();
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse resp, String filepath) throws IOException {
		//Delete file from a specified bucket.
		Storage storage = StorageOptions.getDefaultInstance().getService();
	    storage.delete(BUCKET, filepath);
	}
}
