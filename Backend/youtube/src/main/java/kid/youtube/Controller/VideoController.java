package kid.youtube.Controller;

import kid.youtube.Entity.Category;
import kid.youtube.Entity.Member;
import kid.youtube.Entity.Video;
import kid.youtube.Repository.CategoryRepository;
import kid.youtube.Repository.MemberRepository;
import kid.youtube.Repository.VideoRepository;
import kid.youtube.Service.AuthService;
import kid.youtube.Service.TokenService;
import kid.youtube.Service.UploadService;
import kid.youtube.Service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/videos")
public class VideoController
{
    private Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired private UploadService uploadService;
    @Autowired private AuthService authService;
    @Autowired private TokenService tokenService;
    @Autowired private UtilityService utilityService;

    @Autowired private VideoRepository videoRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Value("${video.location}") private String root;


    @PostMapping("/upload/{category}")
    public ResponseEntity<?> uploadVideo(@CookieValue String AccessToken, @PathVariable String category,  @RequestParam String title, @RequestParam("file") MultipartFile file)
    {
        String id = tokenService.extractUsername(AccessToken);
        String name = file.getOriginalFilename();
        if(Objects.isNull(name) || !utilityService.isType(name, "mp4", "mpeg")) throw new FileTypeError();

        Optional<Category> op_cate = categoryRepository.findCategoryByName(category);
        op_cate.orElseThrow(() -> new RuntimeException("Category Doesn't exist"));
        Category cate = op_cate.get();

        String uuid = uploadService.store(file);
        Video video = new Video(uuid, cate, title, id);
        videoRepository.save(video);

        return ResponseEntity.ok(uuid);
    }

    @DeleteMapping("/delete/{uuid}")
    public String deleteVideo(@CookieValue String AccessToken, @PathVariable String uuid)
    {
        Optional<Video> optionalVideo = videoRepository.findById(uuid);
        optionalVideo.orElseThrow(() -> new RuntimeException("해당 비디오는 존재하지 않습니다."));
        Video video = optionalVideo.get();

        String username = tokenService.extractUsername(AccessToken);
        Optional<Member> optionalMember = memberRepository.findMemberById(username);
        optionalMember.orElseThrow(() -> new UsernameNotFoundException("계정을 확인할 수 없습니다."));

        String uploader = video.getUploader();
        if(authService.hasRole(optionalMember.get(), "ADMIN") && uploader.equals(username))
        {
            uploadService.delete(video.getUuid());
            videoRepository.deleteVideoByUuid(video.getUuid());
            log.info("Video " + uuid + " has been delete by Administrator.");
        }

        return "영상 " + uuid + "는 성공적으로 삭제되었습니다.";
    }

    @GetMapping("/search")
    public List<Video> getCategory(@RequestParam String category)
    {
        Optional<Category> optionalCategory = categoryRepository.findCategoryByName(category);
        optionalCategory.orElseThrow(() -> new RuntimeException("Category Doesn't exist"));

        Category cate = optionalCategory.get();
        return videoRepository.findAllByCategory_Name(cate.getName());
    }

    @GetMapping("/all")
    public List<Video> getVideoList()
    {
        return videoRepository.findAll();
    }

    @GetMapping("/{uuid}")
    public Video getVideo(@PathVariable String uuid)
    {
        Optional<Video> op = videoRepository.findById(uuid);
        op.orElseThrow(() -> new RuntimeException("비디오 " + uuid + "을(를) 찾을 수 없습니다."));
        return op.get();
    }

    @GetMapping("/list")
    public List<String> getFileList() throws IOException
    {
        String videoLocation = root.replace("~", System.getProperty("user.home"));
        Path p = Paths.get(videoLocation);
        return Files
                .list(p)
                .map((path -> path.getFileName().toString()))
                .collect(Collectors.toList());
    }

    @GetMapping("/streaming/{name}")
    public ResponseEntity<ResourceRegion> getVideo(@PathVariable String name, @RequestHeader HttpHeaders headers) throws IOException
    {
        String videoLocation = root.replace("~", System.getProperty("user.home")) + name;
        UrlResource video = new UrlResource("file:" + videoLocation);
        ResourceRegion region = utilityService.resourceRegion(video, headers);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM)).body(region);
    }

    private class FileTypeError extends RuntimeException
    {
        public FileTypeError()
        {
            super("File Doesn't Fit to MP4, Try Again with MP4.");
        }
    }
}
