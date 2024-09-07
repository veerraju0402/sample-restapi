package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.controller.exception.MyCustomException;
import com.example.demo.controller.exception.MyCustomException2;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityManagerFactory;
import com.jmethods.catatumbo.EntityQueryRequest;
import com.jmethods.catatumbo.QueryResponse;

@Service
public class ServiceClass {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceClass.class);
	private static final int BYTE_LENGTH = 10_240;

	@Autowired
	MyRepositry myRepositry;

	// @Autowired
	// GCPDataStore gCPDataStore;

	private Storage storage;
	private EntityManager entityManager;
	

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Autowired
	private ResourceLoader resourceLoader;

	@Value("${service.datastore.credentials}")
	private String credentials;

	@Value("${service.datastore.project-id}")
	private String projectId;

	@Value("${service.datastore.namespace}")
	private String namespace;

	@PostConstruct
	public void init() {
		EntityManagerFactory emf = EntityManagerFactory.getInstance();
		Resource resource = resourceLoader.getResource(credentials);
		try {
			entityManager = emf.createEntityManager(projectId, resource.getInputStream(), namespace);
			System.err.println("entityManager:" + entityManager);
		} catch (IOException e) {
			System.err.println("IOException while loading credentials resource: " + e.getMessage());
			LOGGER.error("IOException while loading credentials resource: {}", e.getMessage());
		}
	}

	public List<Employee> getEmployees() {
		return myRepositry.findAll();
	}

	public Optional<Employee> getEmployee(long id) {
		return myRepositry.findById(id);
	}

	public Employee getEmployee1(long id) {
		return myRepositry.findById(id).orElseThrow(() -> new MyCustomException(id));
	}

	public Employee getEmployee2(long id) {
		return myRepositry.findById(id).orElseThrow(() -> new MyCustomException2(id));
	}

	public Employee saveEmployee(Employee employee) {
		return myRepositry.save(employee);
	}

	public void deleteEmployee(long id) {
		myRepositry.deleteById(id);
	}

	public Page<Employee> findAll(Pageable contactsPageable) {
		return myRepositry.findAll(contactsPageable);
	}

	public List<Employee> getEmployee3(String id) {

		try {
			LOGGER.error("entered method");
			// FirestoreOptions firestoreOptions =
			// FirestoreOptions.getDefaultInstance().toBuilder()
			// .setProjectId(projectId)
			// .setCredentials(GoogleCredentials.getApplicationDefault())
			// .build();
			// Firestore db = firestoreOptions.getService();
			// //to set default credentials
			// //https://cloud.google.com/docs/authentication/provide-credentials-adc
			//
			// // Create a Map to store the data we want to set
			// Map<String, Object> docData = new HashMap<>();
			// docData.put("name", "Los Angeles");
			// docData.put("email", "avr3@gmail.com");
			// docData.put("gender", "male");
			// docData.put("isActive", "false");
			// docData.put("empId", "1234");
			//
			// // Add a new document (asynchronously) in collection "cities" with id "LA"
			// ApiFuture<WriteResult> future =
			// db.collection("my-gcp.collection").document("LA").set(docData);
			// // ...
			// // future.get() blocks on response
			//
			// System.err.println("Update time : " + future.get().getUpdateTime());

			// List<Employee> irstGcpList=gCPDataStore.findByName(id);
			// System.out.println("irstGcpList:"+irstGcpList);

			StringBuilder selectQuery = new StringBuilder("select * from ").append("my-gcp.collection");
			EntityQueryRequest request = null;
			if (StringUtils.isEmpty(id)) {
				request = entityManager.createEntityQueryRequest(selectQuery.toString());
			} else {
				selectQuery.append(" where name = " + id);

				request = entityManager.createEntityQueryRequest(selectQuery.toString());
			}
			QueryResponse<Employee> response = entityManager.executeEntityQueryRequest(Employee.class, request);
			System.err.println("response:" + response);
			return response.getResults();

		} catch (Exception e) {
			System.err.println("error:" + e.getMessage());
			LOGGER.error("error logger:" + e.getMessage());
		}
		return null;
	}

	public void uploadFile(byte[] content, String bucketName, String fileName, String fileType) {
		try {
			BlobId blobId = BlobId.of(bucketName, fileName);
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(fileType).build();
			// Blob blob = storage.create(blobInfo, content);
			uploadToStorage(storage, content, blobInfo);
			LOGGER.info("Successfully uploaded file to bucket :: {} , blobName :: {}", bucketName, fileName);
		} catch (Exception ex) {
			LOGGER.error("Failed to upload file to bucket :: {} , blobName :: {}", bucketName, fileName, ex);
		}
	}

	public void uploadToStorage(Storage storage, byte[] bytes, BlobInfo blobInfo) throws IOException {
		try (WriteChannel writer = storage.writer(blobInfo)) {
			byte[] buffer = new byte[BYTE_LENGTH];
			try (InputStream input = new ByteArrayInputStream(bytes)) {
				int limit;
				while ((limit = input.read(buffer)) >= 0) {
					writer.write(ByteBuffer.wrap(buffer, 0, limit));
				}
			}
		} catch (Exception e) {
			LOGGER.info("Unable to upload to storage {}", e);
		}
	}

}
