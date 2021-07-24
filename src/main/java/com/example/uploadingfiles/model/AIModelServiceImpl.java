package com.example.uploadingfiles.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AIModelServiceImpl implements ModelService {
	private final Path rootLocation;
	private final RestTemplate restTemplate;

	@Autowired
	public AIModelServiceImpl() throws IOException {
		rootLocation = Paths.get("PressureProfile");
		//TODO: check why is this not working.
	//	start the python port.
		String cmd = "python src/main/python/AImodelService.py";
		System.out.println(cmd);
		Runtime.getRuntime().exec(cmd);

		restTemplate = new RestTemplate();
		//restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://127.0.0.1:5000"));

	}

	@Override
	public void generatePressureProfile(Path file) {
       // send the loc to the model to generate pic
		// Rest API call. //Post or Put use map of string: path.

		System.out.println("generate Pressure profile is called");
		//url
		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl("http://127.0.0.1:5000/model/getpressure");
		//header
		HttpHeaders headers = new HttpHeaders();
		// ContentType is different depending on the type of headers .. JUST COPY Pasted
	//	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// body
		HashMap<String, String> postData=new HashMap<>();
		postData.put("file_path", String.valueOf(file.getParent()));
		postData.put("filename", String.valueOf(file.getFileName()));

		HttpEntity<HashMap<String, String>> entity
				= new HttpEntity<>(postData, headers);

		System.out.println("psot call start");
		//restTemplate.postForEntity(urlBuilder.toUriString(),postData, JSONObject.class);
		restTemplate.exchange(urlBuilder.toUriString(),HttpMethod.POST,entity, JSONObject.class);
		System.out.println("post call end");


	/*try {
		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl("http://127.0.0.1:5000/model/getpressure");
		HttpEntity<String> response = restTemplate.exchange(urlBuilder.toUriString(), HttpMethod.POST, new HttpEntity(new HashMap<String, String>(){{put("file_path", String.valueOf(file.getParent()));put("filename", String.valueOf(file.getFileName()));}}, new HttpHeaders()), String.class);
	/*	JSONObject result = new JSONObject(response.getBody());
		//return TRUE;
	} catch (JsonProcessingException js) {
		//return new WebsiteOptimizationResponse(Boolean.FALSE);
	}*/
	}

	@Override
	public Resource getPressurePath(String filename)
	{
        // get the path of the image.

		try {
			Path file = rootLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

}