package Helpers;

import Enums.Language;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.assertj.core.data.Percentage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class TestDataHelper {
    public static final String TEST_DATA_FOLDER_PATH = "src//main//java//data//testdata//";
    public static final String EXPIRED_TOKEN_PATH = "//token//";
    public static final String JSON_SCHEMA_FOLDER_PATH = "jsonschemas//";
    private static final String TEST_DATA_NAMESPACE = "data.testdata.";
    private static final int BUFFER_SIZE = 4096;

    public static final String TEST_SCRIPTS_FOLDER_PATH = "src//scripts//";

    public static String getJsonSchemaFolderPath(String fileName) throws IOException {
        return Paths.get(TEST_DATA_FOLDER_PATH, JSON_SCHEMA_FOLDER_PATH, fileName).toString();
    }

    public static String readJsonSchemaFile(String fileName) throws IOException {
        return Files.readString(Paths.get(TEST_DATA_FOLDER_PATH, JSON_SCHEMA_FOLDER_PATH, fileName));
    }

    public static String getTestDataResourceValue(String resource, Language language) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        if (resource.contains("TestData")) {
            var className = resource.split("\\.")[0];
            var resourceName = resource.split("\\.")[1].toUpperCase();
            var classObject = Class.forName(TEST_DATA_NAMESPACE + className);
            var fieldObject = classObject.getField(resourceName);

            return ((Map<Language, String>) fieldObject.get(classObject)).get(language);
        } else {
            return resource;
        }
    }

    public static void replaceInFile(String filePath, String oldString, String newString) throws IOException {
        replaceInFile(filePath, filePath, oldString, newString);
    }

    public static void replaceInFile(String fileToBeModifiedPath, String outputFilePath, String oldString, String newString) throws IOException {
        File fileToBeModified = new File(fileToBeModifiedPath);
        StringBuilder oldContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(fileToBeModified));
        String line = reader.readLine();

        while (line != null) {
            oldContent.append(line).append(System.lineSeparator());
            line = reader.readLine();
        }
        String newContent = oldContent.toString().replaceAll(oldString, newString);
        FileWriter writer = new FileWriter(outputFilePath);
        writer.write(newContent);
        reader.close();
        writer.close();
    }

    public static void createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static void createZipFile(String zipFilePath, String fileToZip) throws Exception {
        try (
                FileOutputStream fos = new FileOutputStream(zipFilePath);
                ZipOutputStream zos = new ZipOutputStream(fos);
                FileInputStream fis = new FileInputStream(fileToZip)
        ) {
            ZipEntry zipEntry = new ZipEntry(new File(fileToZip).getName());
            zos.putNextEntry(zipEntry);

            // Write the content of the file to the zip file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();

        } catch (IOException e) {
            throw new Exception("Zip file could not be created");
        }
    }

    public static void deleteZipFile(String zipFilePath) {
        File zipFile = new File(zipFilePath);
        if (zipFile.exists()) {
            zipFile.delete();
        }
    }

    public static void unzipFile(String ZipFilePath) throws IOException {
        unzipFile(ZipFilePath, new File(ZipFilePath).getParent());
    }

    public static void unzipFile(String ZipFilePath, String DestFilePath) throws IOException {
        createDirectory(DestFilePath);
        ZipInputStream Zip_Input_Stream = new ZipInputStream(new FileInputStream(ZipFilePath));
        ZipEntry Zip_Entry = Zip_Input_Stream.getNextEntry();
        while (Zip_Entry != null) {
            String File_Path = Paths.get(DestFilePath, Zip_Entry.getName()).toString();
            if (!Zip_Entry.isDirectory()) {
                extractFile(Zip_Input_Stream, File_Path);
            } else {
                File directory = new File(File_Path);
                directory.mkdirs();
            }
            Zip_Input_Stream.closeEntry();
            Zip_Entry = Zip_Input_Stream.getNextEntry();
        }
        Zip_Input_Stream.close();
    }

    public static void unzipFileWithPassword(String zipFilePath, String password) throws IOException {
        unzipFileWithPassword(zipFilePath, new File(zipFilePath).getParent(), password);
    }

    public static void unzipFileWithPassword(String zipFilePath, String destinationPath, String password) throws IOException {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.setPassword(password.toCharArray());
            destinationPath = Paths.get(destinationPath, zipFile.getFile().getName().replaceAll(".zip", "")).toString();

            zipFile.extractAll(destinationPath);
        } catch (ZipException e) {
            System.out.println("Unable to extract zip file: " + e.getMessage());
        }
    }

    public static String getLastModifiedFilePathAtFolder(String folderPath, String fileFilterValue) {
        File dir = new File(Paths.get(folderPath).toAbsolutePath().toString());
        FileFilter fileFilter = new WildcardFileFilter(fileFilterValue, IOCase.INSENSITIVE);
        File[] files = dir.listFiles(fileFilter);
        if (!dir.exists() || files.length == 0) {
            return null;
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return Paths.get(files[0].getAbsolutePath()).toAbsolutePath().toString();
    }

    private static void extractFile(ZipInputStream Zip_Input_Stream, String File_Path) throws IOException {
        BufferedOutputStream Buffered_Output_Stream = new BufferedOutputStream(new FileOutputStream(File_Path));
        byte[] Bytes = new byte[BUFFER_SIZE];
        int Read_Byte = 0;
        while ((Read_Byte = Zip_Input_Stream.read(Bytes)) != -1) {
            Buffered_Output_Stream.write(Bytes, 0, Read_Byte);
        }
        Buffered_Output_Stream.close();
    }

    public static void createDirectory(String directory) {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void cleanDirectory(String folderPath) {
        File directory = new File(folderPath);
        if (directory.exists()) {
            try {
                FileUtils.cleanDirectory(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void cleanOrCreateDirectory(String directoryPath) throws IOException {
        RetryPolicies.executeActionWithRetries(() -> {
            if (new File(directoryPath).exists()) {
                TestDataHelper.cleanDirectory(directoryPath);
            } else {
                TestDataHelper.createDirectory(directoryPath);
            }
        });
    }

    public static void base64ToFile(String base64Content, String directoryPath, String fileName) throws IOException {
        createDirectory(directoryPath);
        File outputFile = new File(directoryPath, fileName);
        FileOutputStream fos = new FileOutputStream(outputFile);
        byte[] decodedContent = Base64.getDecoder().decode(base64Content);
        fos.write(decodedContent);
        fos.close();
    }

    public static void inputStreamToFile(InputStream inputStream, String directoryPath, String fileName) throws IOException {
        createDirectory(directoryPath);
        File outputFile = new File(directoryPath, fileName);
        FileOutputStream fos = new FileOutputStream(outputFile);
        IOUtils.copy(inputStream, fos);
        fos.close();
    }


    public static void unzipInputStream(InputStream inputStream, String directoryPath, String fileName) throws IOException {
        ZipInputStream zis = new ZipInputStream(inputStream);
        TestDataHelper.createDirectory(directoryPath);
        File outputFile = new File(directoryPath, fileName);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            File newFile = new File(outputFile, entry.getName());
            if (newFile.getParentFile().mkdirs() && !entry.isDirectory()) {
                FileOutputStream fos = new FileOutputStream(newFile);
                IOUtils.copy(zis, fos);
                fos.close();
            }
        }
        zis.close();
    }

    public static void setHardDiskFreePercentageSpace(Integer percentageToFillUpTo) throws IOException {
        var hdCapacityInByte = new File("/").getTotalSpace();
        var currentFreeSpaceInByte = FileSystemUtils.freeSpaceKb("/") * 1024;
        var expectedFreeSpaceInByte = (hdCapacityInByte - hdCapacityInByte * percentageToFillUpTo / 100);
        var spaceToFillInByte = currentFreeSpaceInByte - expectedFreeSpaceInByte;
        assertThat(spaceToFillInByte >= 0)
                .withFailMessage("please free space on HD, it is not possible to set the free HD percentage space, freespace:%s, expectedSpace:%S", currentFreeSpaceInByte, expectedFreeSpaceInByte).isTrue();
        createTempFile(ConfigFileReader.getDownloadDataPath(), "temp.dat", spaceToFillInByte);
    }

    public static void setHardDiskFreeSpace(Integer freeSpaceInGb) throws IOException, InterruptedException {
        var currentFreeSpaceInByte = FileSystemUtils.freeSpaceKb("/") * 1024;
        var spaceToFillInByte = currentFreeSpaceInByte - (freeSpaceInGb * 1073741824L);
        createTempFile(ConfigFileReader.getDownloadDataPath(), "temp.dat", spaceToFillInByte);
        Thread.sleep(1000);
    }

    public static void createTempFile(String folderPath, String fileName, long spaceToFillInByte) throws IOException {
        var filePath = Paths.get(folderPath, fileName);
        CommandLineHelper.executeCommand(String.format("fsutil file createnew %s %s", filePath, spaceToFillInByte));
    }

    public static void checkFileExists(String filePath) {
        File file = new File(filePath);
        assertThat(file).isNotNull();
        assertThat(file.exists()).isTrue();
        assertThat(file.length()).isGreaterThan(0);
    }

    public static void checkFileExistsInFolderWithFileSize(String folderPath, String fileName, long fileSize) {
        File folder = new File(folderPath);
        File file = new File(folder, fileName);

        RetryPolicies.executeActionWithRetries(() -> {
            if (folder.exists() && folder.isDirectory()) {
                if (file.exists() && file.isFile()) {
                    assertThat(file.length()).isCloseTo(fileSize, Percentage.withPercentage(10));
                } else {
                    throw new NullPointerException("File has not been created");
                }
            } else {
                throw new NullPointerException("Folder does not exist");
            }
        });
    }

    public static String readFileAsString(String filePath) throws IOException {
        checkFileExists(filePath);
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static boolean checkFileExistsInFolderAndSubfolders(String containerFolderPath, String fileOrFolderNameToSearch) {
        File folder = new File(containerFolderPath);
        boolean containsFileOrFolder = false;
        if (folder.isDirectory()) {
            File[] filesAndFolders = folder.listFiles();

            if (filesAndFolders != null) {
                Pattern pattern = Pattern.compile(fileOrFolderNameToSearch);

                for (File item : filesAndFolders) {
                    if (pattern.matcher(item.getName()).matches()) {
                        containsFileOrFolder = true;
                        break;
                    }
                    if (item.isDirectory()) {
                        if (checkFileExistsInFolderAndSubfolders(item.getPath(), fileOrFolderNameToSearch)) {
                            return true;
                        }
                    }
                }
            }
        } else {
            throw new NullPointerException("Error: The specified path is not a directory.");
        }
        return containsFileOrFolder;
    }

    public static String findFileInFolderAndSubfolders(String containerFolderPath, String fileOrFolderNameToSearch) {
        File folder = new File(containerFolderPath);

        if (folder.isDirectory()) {
            File[] filesAndFolders = folder.listFiles();

            if (filesAndFolders != null) {
                Pattern pattern = Pattern.compile(fileOrFolderNameToSearch);

                for (File item : filesAndFolders) {
                    if (pattern.matcher(item.getName()).matches()) {
                        return item.getAbsolutePath();
                    }

                    if (item.isDirectory()) {
                        String foundFilePath = findFileInFolderAndSubfolders(item.getPath(), fileOrFolderNameToSearch);
                        if (foundFilePath != null) {
                            return foundFilePath;
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Error: The specified path is not a directory.");
        }
        return null;
    }

    public static void deleteFile(String filePath) {
        File fileToDelete = new File(filePath);
        if (fileToDelete.exists()) {
            try {
                FileUtils.forceDelete(fileToDelete);
                System.out.println("File deleted: " + filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
