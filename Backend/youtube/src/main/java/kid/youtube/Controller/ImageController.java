package kid.youtube.Controller;

import kid.youtube.Service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/image")
public class ImageController
{
    @Autowired
    private UploadService uploadService;
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
