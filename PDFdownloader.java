import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFdownloader {

	public static void main(String[] args) {

		String website = args[0];

		PDFdownloader p = new PDFdownloader();

		String page;

		try {
			URL pageurl = new URL(website);
			page = p.downloadPage(pageurl);
			ArrayList<String> links = p.findHyperLinks(page);
			pageurl = new URL(website);
			ArrayList<URL> list = p.findPDFs(links, pageurl);
			System.out.println(list);
			for(URL url : list){
				p.downloadPDF(url);
			}

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public PDFdownloader() {
	}

	public void downloadPDF(URL url){
		InputStream in;
		try {
			in = url.openStream();

		FileOutputStream fos;
		String filename = url.toString();
		int index = filename.lastIndexOf("/");
		filename = filename.substring(index + 1);
		File file = new File(filename);
			fos = new FileOutputStream(file);


		int length = -1;
		byte[] buffer = new byte[1024];// buffer for portion of data from connection
		while ((length = in.read(buffer)) > -1) {
		    fos.write(buffer, 0, length);
		}
		fos.close();
		in.close();
		System.out.println("Downloaded " +filename);} catch (IOException e) {
			System.out.println("404 File not found");
		}
	}

	public String downloadPage(URL url) {

		InputStream input = null;
		StringBuilder content = new StringBuilder();

		try {
			input = url.openStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(input));

			String inputLine;

			while ((inputLine = in.readLine()) != null) {

				// System.out.println(inputLine);

				content.append(inputLine);
			}

			input.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content.toString();

	}

	public ArrayList<String> findHyperLinks(String page) {

		ArrayList<String> links = new ArrayList<String>();

		// <a> tags
		Pattern aTags = Pattern.compile("<a(.*?)/a>");
		Matcher aMatcher = aTags.matcher(page);

		while (aMatcher.find()) {

			String aTag = aMatcher.group();

			// hrefs
			Pattern hrefs = Pattern.compile("href=\"(.*?)\"");
			Matcher hrefMatcher = hrefs.matcher(aTag);

			if (hrefMatcher.find()) {
				String link = hrefMatcher.group(1);
				links.add(link);
			}

		}

		return links;
	}

	public URL makeAbsolute(URL baseURL, String link){
		Pattern httpPattern = Pattern.compile("^http(.*?)");
		Matcher httpMatcher = httpPattern.matcher(link);


			if(!(httpMatcher.find()) && link != null){
				try {
					return(new URL(baseURL, link));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		return baseURL;
	}

	public ArrayList<URL> findPDFs(ArrayList<String> links, URL baseURL) {

		ArrayList<URL> pdfUrls = new ArrayList<URL>();

		for (String link : links) {

			// ends with pdf
			Pattern pdfPattern = Pattern.compile("(.*?).pdf");
			Matcher pdfMatcher = pdfPattern.matcher(link);

			while (pdfMatcher.find()) {
						pdfUrls.add(makeAbsolute(baseURL, pdfMatcher.group()));
			}

		}
		return pdfUrls;
	}
	private boolean isPdf(URL u) {

		System.out.println(u.getPath());
		System.out.println(u.getFile());

		return false;
	}

	public void saveStreamToFile(InputStream input) {

		File file = null;
		try {
			FileOutputStream out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
