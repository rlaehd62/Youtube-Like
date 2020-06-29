package kid.youtube.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class UploadService
{
    @Value("${video.location}")
    private String root;

    @Autowired
    private VideoUtilService videoUtilService;

    public String store(MultipartFile file)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String filename = uuid + ".mp4";
        String storeURL = "http://localhost:8080/video/streaming/" + filename;

        try
        {
            if (file.isEmpty()) throw new StorageException("Failed to store empty file " + filename);
            if (filename.contains(".."))
                throw new StorageException
                    (
                            "Cannot store file with relative path outside current directory "
                                    + filename
                    );

            try (InputStream inputStream = file.getInputStream())
            {
                Path rootPath = Paths.get(root.replace("~", System.getProperty("user.home")));
                Files.copy(inputStream, rootPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                videoUtilService.captureVideo(rootPath.resolve(filename).toString(), rootPath, uuid);
            }
        }
        catch (IOException e)
        { throw new StorageException("Failed to store file " + filename, e); }

        return uuid;
        // return storeURL;    // 추후에 URL 대신 UUID 반환 (이미 파일은 업로드 했기 때문에 UUID 만 있으면 영상 스트리밍 가능)
    }

    public String storeImage(MultipartFile file)
    {
        String filename = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        String storeURL = "http://localhost:8080/image/streaming/" + filename;

        try
        {
            if (file.isEmpty()) throw new StorageException("Failed to store empty file " + filename);
            if (filename.contains(".."))
                throw new StorageException
                        (
                                "Cannot store file with relative path outside current directory "
                                        + filename
                        );

            try (InputStream inputStream = file.getInputStream())
            {
                Path rootPath = Paths.get(root.replace("~", System.getProperty("user.home")));
                Files.copy(inputStream, rootPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e)
        { throw new StorageException("Failed to store file " + filename, e); }

        return storeURL;
    }

    public Stream<Path> loadAll()
    {
        try
        {
            Path rootPath = Paths.get(root.replace("~", System.getProperty("user.home")));
            return Files.walk(rootPath, 1)
                    .filter(path -> !path.equals(rootPath))
                    .map(rootPath::relativize);
        }
        catch (IOException e)
        { throw new StorageException("Failed to read stored files", e); }

    }

    public Path load(String filename)
    {
        Path rootPath = Paths.get(root.replace("~", System.getProperty("user.home")));
        return rootPath.resolve(filename);
    }

    public Resource loadAsResource(String filename)
    {
        try
        {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) return resource;
            else
            {
                throw new StorageFileNotFoundException
                        (
                                "Could not read file: " + filename
                        );
            }
        }

        catch (MalformedURLException e)
        {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    public void delete(String uuid)
    {
        try
        {
            String file1 = uuid+".mp4";
            String file2 = uuid+".jpg";

            Path rootPath = Paths.get(root.replace("~", System.getProperty("user.home")));
            Files.deleteIfExists(rootPath.resolve(file1));
            Files.deleteIfExists(rootPath.resolve(file2));
        } catch (Exception e)
        { e.printStackTrace(); }
    }

    private class StorageException extends RuntimeException
    {

        public StorageException(String message)
        {
            super(message);
        }

        public StorageException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }

    private class StorageFileNotFoundException extends StorageException
    {

        public StorageFileNotFoundException(String message)
        {
            super(message);
        }

        public StorageFileNotFoundException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }
}
