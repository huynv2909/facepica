package org.huynv.facepica;

import java.util.List;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DeleteCollectionResult;
import com.amazonaws.services.rekognition.model.DescribeCollectionRequest;
import com.amazonaws.services.rekognition.model.DescribeCollectionResult;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;
import com.amazonaws.services.rekognition.model.ListFacesRequest;
import com.amazonaws.services.rekognition.model.ListFacesResult;
import com.amazonaws.services.rekognition.model.QualityFilter;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.rekognition.model.SearchFacesRequest;
import com.amazonaws.services.rekognition.model.SearchFacesResult;
import com.amazonaws.services.rekognition.model.UnindexedFace;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Collection {

    public static CreateCollectionResult createCollection(String collection_name) {

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

        CreateCollectionRequest request = new CreateCollectionRequest()
                .withCollectionId(collection_name);

        CreateCollectionResult createCollectionResult = new CreateCollectionResult();
        try {
            createCollectionResult = rekognitionClient.createCollection(request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return createCollectionResult;
    }

    public static List < String > listCollections() {
        AmazonRekognition amazonRekognition = AmazonRekognitionClientBuilder.defaultClient();

        int limit = 10;
        ListCollectionsResult listCollectionsResult = null;

        ListCollectionsRequest listCollectionsRequest = new ListCollectionsRequest()
                .withMaxResults(limit);
        listCollectionsResult = amazonRekognition.listCollections(listCollectionsRequest);

        List < String > collectionIds = listCollectionsResult.getCollectionIds();

        return collectionIds;
    }

    public static DescribeCollectionResult describeCollection(String collection_name) {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

        DescribeCollectionResult describeCollectionResult = new DescribeCollectionResult();


        DescribeCollectionRequest request = new DescribeCollectionRequest()
                .withCollectionId(collection_name);


        try {
            describeCollectionResult = rekognitionClient.describeCollection(request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return describeCollectionResult;
    }

    public static DeleteCollectionResult deleteCollection(String collection_name) {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

        DeleteCollectionResult deleteCollectionResult = new DeleteCollectionResult();

        DeleteCollectionRequest request = new DeleteCollectionRequest()
                .withCollectionId(collection_name);

        try {
            deleteCollectionResult = rekognitionClient.deleteCollection(request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return deleteCollectionResult;

    }

    public static ResultObject addFacesToCollection(String collection_name, String bucket_name, String image_name) {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
        ResultObject result = new ResultObject("failed");

        Image image = new Image()
                .withS3Object(new S3Object()
                        .withBucket(bucket_name)
                        .withName(image_name));

        IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
                .withImage(image)
                .withQualityFilter(QualityFilter.AUTO)
                .withMaxFaces(1)
                .withCollectionId(collection_name)
                .withExternalImageId(image_name)
                .withDetectionAttributes("DEFAULT");

        try {
            IndexFacesResult indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);

            result.setInfo("Results for " + image_name + ". Faces indexed id:");

            List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
            for (FaceRecord faceRecord : faceRecords) {
                result.setObject(faceRecord.getFace().getFaceId());
            }

            result.setStatus("success");
        } catch (Exception e) {
            result.setInfo(e.getMessage());
        }

        return result;

    }

    public static ResultObject listingFacesInCollection(String collection_name) throws Exception {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

        ResultObject result = new ResultObject("failed");
        ObjectMapper objectMapper = new ObjectMapper();

        ListFacesResult listFacesResult = null;
        result.setInfo("Faces in collection " + collection_name);

        String paginationToken = null;

        try {
            do {
                if (listFacesResult != null) {
                    paginationToken = listFacesResult.getNextToken();
                }

                ListFacesRequest listFacesRequest = new ListFacesRequest()
                        .withCollectionId(collection_name)
                        .withMaxResults(1)
                        .withNextToken(paginationToken);

                listFacesResult =  rekognitionClient.listFaces(listFacesRequest);
                List < Face > faces = listFacesResult.getFaces();
                for (Face face: faces) {
                    result.setObject(objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(face));
                }
            } while (listFacesResult != null && listFacesResult.getNextToken() !=
                    null);

            result.setStatus("success");
        } catch (Exception e) {
            result.setInfo(e.getMessage());
        }

        return result;
    }


    public static SearchFacesByImageResult searchFaceMatchingImageCollection(String collection_name, String bucket_name, String photo) throws Exception {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

        ResultObject result = new ResultObject("failed");
        ObjectMapper objectMapper = new ObjectMapper();

        // Get an image object from S3 bucket.
        Image image=new Image()
                .withS3Object(new S3Object()
                        .withBucket(bucket_name)
                        .withName(photo));

        // Search collection for faces similar to the largest face in the image.
        SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                .withCollectionId(collection_name)
                .withImage(image)
                .withFaceMatchThreshold(95F)
                .withMaxFaces(1);

        return rekognitionClient.searchFacesByImage(searchFacesByImageRequest);

    }

}
