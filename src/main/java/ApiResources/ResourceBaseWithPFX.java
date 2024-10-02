package ApiResources;

import Helpers.TestDataHelper;
import com.google.common.net.HttpHeaders;
import Helpers.ConfigFileReader;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResourceBaseWithPFX {
    private static final String WORKING_CHANNEL_HEADER = "Working-Channel";

    protected static CloseableHttpClient client = null;
    public static String outputZipName = "output.zip";
    public static Boolean unzipBody = false;


    public ResourceBaseWithPFX(String pfxFilePath, String pfxPassword) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(pfxFilePath)) {
                keyStore.load(fis, pfxPassword.toCharArray());
            }

            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadKeyMaterial(keyStore, pfxPassword.toCharArray())
                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                    .build();

            client = HttpClients
                    .custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(30 * 1000).build())
                    .build();
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException |
                 UnrecoverableKeyException e) {
            // Handle exceptions appropriately (log, throw, etc.)
            e.printStackTrace();
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpEntity getEntity(CloseableHttpResponse response) {
        return response.getEntity();
    }

    public static String getBody(CloseableHttpResponse response) throws IOException {
        var entity = response.getEntity();
        String responseBody = "";
        if (entity != null) {
            if (entity.getContentType() != null && (entity.getContentType().getValue().contains("zip") || entity.getContentType().getValue().contains("octet-stream"))) {
                if (unzipBody) {
                    TestDataHelper.unzipInputStream(entity.getContent(), ConfigFileReader.getDownloadDataPath(), outputZipName);
                } else {
                    TestDataHelper.inputStreamToFile(entity.getContent(), ConfigFileReader.getDownloadDataPath(), outputZipName);
                }
                responseBody = "(Comment added by Validation team) Response body is a zip stream and it has been downloaded in " + ConfigFileReader.getDownloadDataPath();
            } else {
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
        }

        return responseBody;
    }

    public static int getStatusCode(CloseableHttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    protected static Map.Entry<String, CloseableHttpResponse> executeGetRequest(String endpoint, String bearerToken) throws IOException {
        return executeGetRequest(endpoint, "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executeGetRequest(String endpoint, String workingChannel, String bearerToken) throws IOException {
        return executeGetRequest(endpoint, workingChannel, "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executeGetRequest(String endpoint, String workingChannel, String body, String bearerToken) throws IOException {
        return executeGetRequest(endpoint, workingChannel, body, null, bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executeGetRequest(String endpoint, String workingChannel, String body, Map<String, String> headerList, String bearerToken) throws IOException {
        return executeGetRequest(endpoint, workingChannel, body, headerList, ContentType.APPLICATION_JSON, bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executeGetRequest(String endpoint, String workingChannel, String body, Map<String, String> headerList, ContentType contentType, String bearerToken) throws IOException {
        RequestBuilder request = !Objects.equals(workingChannel, "")
                ? RequestBuilder.get(endpoint).setHeader(WORKING_CHANNEL_HEADER, workingChannel)
                : RequestBuilder.get(endpoint);

        if (headerList != null) {
            for (Map.Entry<String, String> header : headerList.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }

        HttpEntity bodyEntity = new StringEntity(body, contentType);
        request.setEntity(bodyEntity);

        ;
        return generateReturnObject(request, client.execute(createRequestFromBuilder(request, bearerToken)));
    }


    protected static Map.Entry<String, CloseableHttpResponse> executePostRequest(String endpoint, String bearerToken) throws IOException {
        return executePostRequest(endpoint, "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePostRequest(String endpoint, String body, String bearerToken) throws IOException {
        return executePostRequest(endpoint, body, "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePostRequest(String endpoint, String body, String workingChannel, String bearerToken) throws IOException {
        return executePostRequest(endpoint, body, workingChannel, null, bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePostRequest(String endpoint, String body, String workingChannel, Map<String, String> headerList, String bearerToken) throws IOException {
        return executePostRequest(endpoint, body, workingChannel, headerList, ContentType.APPLICATION_JSON, bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePostRequest(String endpoint, String workingChannel, Map<String, String> headerList, File file, String bearerToken) throws IOException {
        RequestBuilder request = !Objects.equals(workingChannel, "")
                ? RequestBuilder.post(endpoint).setHeader(WORKING_CHANNEL_HEADER, workingChannel)
                : RequestBuilder.post(endpoint);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        FileBody fileBody = new FileBody(file);
        builder.addPart("file", fileBody);

        if (headerList != null) {
            for (Map.Entry<String, String> header : headerList.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }

        request.setEntity(builder.build());
        return generateReturnObject(request, client.execute(createRequestFromBuilder(request, "", bearerToken)));
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePostRequest(String endpoint, String body, String workingChannel, Map<String, String> headerList, ContentType contentType, String bearerToken) throws IOException {
        RequestBuilder request = !Objects.equals(workingChannel, "")
                ? RequestBuilder.post(endpoint).setHeader(WORKING_CHANNEL_HEADER, workingChannel)
                : RequestBuilder.post(endpoint);

        if (headerList != null) {
            for (Map.Entry<String, String> header : headerList.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }

        if (body != null) {
            HttpEntity bodyEntity = new StringEntity(body, contentType);
            request.setEntity(bodyEntity);
        }

        return generateReturnObject(request, client.execute(createRequestFromBuilder(request, bearerToken)));
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePostRequest(String endpoint, List<NameValuePair> body, String workingChannel, Map<String, String> headerList, String bearerToken) throws IOException {
        RequestBuilder request = !Objects.equals(workingChannel, "")
                ? RequestBuilder.post(endpoint).setHeader(WORKING_CHANNEL_HEADER, workingChannel)
                : RequestBuilder.post(endpoint);

        if (headerList != null) {
            for (Map.Entry<String, String> header : headerList.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }

        request.setEntity(new UrlEncodedFormEntity(body));

        return generateReturnObject(request, client.execute(createRequestFromBuilder(request, ContentType.APPLICATION_FORM_URLENCODED, bearerToken)));
    }

    protected static Map.Entry<String, CloseableHttpResponse> executePatchRequest(String endpoint, String bearerToken) throws IOException {
        return executePatchRequest(endpoint, "", "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePatchRequest(String endpoint, String body, String bearerToken) throws IOException {
        return executePatchRequest(endpoint, body, "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePatchRequest(String endpoint, String body, String workingChannel, String bearerToken) throws IOException {
        RequestBuilder request = !Objects.equals(workingChannel, "")
                ? RequestBuilder.patch(endpoint).setHeader(WORKING_CHANNEL_HEADER, workingChannel)
                : RequestBuilder.patch(endpoint);
        HttpEntity bodyEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(bodyEntity);

        return generateReturnObject(request, client.execute(createRequestFromBuilder(request, bearerToken)));
    }

    protected static Map.Entry<String, CloseableHttpResponse> executePutRequest(String endpoint, String bearerToken) throws IOException {
        return executePutRequest(endpoint, "", "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePutRequest(String endpoint, String body, String bearerToken) throws IOException {
        return executePutRequest(endpoint, body, "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executePutRequest(String endpoint, String body, String workingChannel, String bearerToken) throws IOException {
        RequestBuilder request = !Objects.equals(workingChannel, "")
                ? RequestBuilder.put(endpoint).setHeader(WORKING_CHANNEL_HEADER, workingChannel)
                : RequestBuilder.put(endpoint);
        HttpEntity bodyEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(bodyEntity);

        return generateReturnObject(request, client.execute(createRequestFromBuilder(request, bearerToken)));
    }


    protected static Map.Entry<String, CloseableHttpResponse> executeDeleteRequest(String endpoint, String bearerToken) throws IOException {
        return executeDeleteRequest(endpoint, "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executeDeleteRequest(String endpoint, String workingChannel, String bearerToken) throws IOException {
       return executeDeleteRequest(endpoint, workingChannel, "", bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executeDeleteRequest(String endpoint, String workingChannel, String body, String bearerToken) throws IOException {
        return executeDeleteRequest(endpoint, workingChannel, body, null, bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executeDeleteRequest(String endpoint, String workingChannel, String body, Map<String, String> headerList, String bearerToken) throws IOException {
        return executeDeleteRequest(endpoint, workingChannel, body, headerList, ContentType.APPLICATION_JSON, bearerToken);
    }
    protected static Map.Entry<String, CloseableHttpResponse> executeDeleteRequest(String endpoint, String workingChannel, String body, Map<String, String> headerList, ContentType contentType, String bearerToken) throws IOException {
        RequestBuilder request = !Objects.equals(workingChannel, "")
                ? RequestBuilder.delete(endpoint).setHeader(WORKING_CHANNEL_HEADER, workingChannel)
                : RequestBuilder.delete(endpoint);

        if (headerList != null) {
            for (Map.Entry<String, String> header : headerList.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }

        HttpEntity bodyEntity = new StringEntity(body, contentType);
        request.setEntity(bodyEntity);

        return generateReturnObject(request, client.execute(createRequestFromBuilder(request, bearerToken)));
    }

    protected static String createEndpoint(String endpoint) {
        return endpoint;
    }

    private static HttpUriRequest createRequestFromBuilder(RequestBuilder requestBuilder, String bearerToken) {
        return createRequestFromBuilder(requestBuilder, ContentType.APPLICATION_JSON, bearerToken);
    }
    private static HttpUriRequest createRequestFromBuilder(RequestBuilder requestBuilder, ContentType contentType, String bearerToken) {
        return createRequestFromBuilder(requestBuilder, contentType.toString(), bearerToken);
    }

    private static HttpUriRequest createRequestFromBuilder(RequestBuilder requestBuilder, String contentType, String bearerToken) {
        if (contentType != null && !Objects.equals(contentType, "")) {
            requestBuilder.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
        }

        if (bearerToken != null && !Objects.equals(bearerToken, "")) {
            requestBuilder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        }


        return requestBuilder.build();
    }

    private static Map.Entry<String, CloseableHttpResponse> generateReturnObject(RequestBuilder request, CloseableHttpResponse response) {
        return new AbstractMap.SimpleEntry<>(request.getUri().toString(), response);
    }

    public void closeClient() throws IOException {
        if (client != null) {
            client.close();
        }
    }
}
