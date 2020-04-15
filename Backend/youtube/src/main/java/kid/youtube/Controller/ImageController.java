package kid.youtube.Controller;

import kid.youtube.Service.YoutubeUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/image")
@CrossOrigin
        (
                origins = "http://localhost:3000",
                allowCredentials = "true",
                allowedHeaders = {"Origin", "Content-Type", "X-Auth-Token"},
                methods =
                        {
                                RequestMethod.GET,
                                RequestMethod.POST,
                                RequestMethod.PUT,
                                RequestMethod.DELETE,
                                RequestMethod.PATCH,
                                RequestMethod.OPTIONS,
                                RequestMethod.HEAD,
                                RequestMethod.TRACE
                        }
        )
public class ImageController
{
    @Autowired
    private YoutubeUploadService uploadService;
    private Logger log = Logger.getLogger(this.getClass().getName());

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file)
    {
        return uploadService.storeImage(file);
    }

    @GetMapping("/streaming/{name}")
    public ResponseEntity<Resource> getImage(@PathVariable String name) throws IOException
    {
        Resource file = uploadService.loadAsResource(name);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
