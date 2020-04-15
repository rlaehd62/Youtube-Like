package kid.youtube.Controller;

import kid.youtube.Entity.Category;
import kid.youtube.Entity.Member;
import kid.youtube.Entity.Video;
import kid.youtube.Repository.MemberRepository;
import kid.youtube.Repository.VideoRepository;
import kid.youtube.Service.TokenService;
import kid.youtube.Service.YoutubeUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/videos")
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
public class VideoController
{

    @Autowired
    private YoutubeUploadService uploadService;
    private Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired
    private TokenService tokenService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Value("${video.location}")
    private String root;

    @PostMapping("/upload/{category}")
    public ResponseEntity<?> uploadVideo(@CookieValue String AccessToken, @PathVariable Category category,  @RequestParam String title, @RequestParam("file") MultipartFile file)
    {
        String id = tokenService.extractUsername(AccessToken);
        String name = file.getOriginalFilename();
        if(!name.contains(".mp4")) throw new FileTypeError();

        String uuid = uploadService.store(file);
        Video video = new Video(uuid, category, title, id);
        videoRepository.save(video);

        return ResponseEntity.ok(uuid);
    }

    @DeleteMapping("/delete/{uuid}")
    public String deleteVideo(@CookieValue String AccessToken, @PathVariable String uuid)
    {
        Optional<Video> op_video = videoRepository.findById(uuid);
        op_video.orElseThrow(() -> new RuntimeException("해당 비디오는 존재하지 않습니다."));
        Video video = op_video.get();


        String username = tokenService.extractUsername(AccessToken);
        Optional<Member> op_member = memberRepository.findMemberById(username);
        op_member.orElseThrow(() -> new UsernameNotFoundException("계정을 확인할 수 없습니다."));
        List<SimpleGrantedAuthority> roles = getRoles(op_member.get());

        SimpleGrantedAuthority role = new SimpleGrantedAuthority("ROLE_ADMIN");
        String uploader = video.getUploader();
        if(roles.contains(role) || uploader.equals(username)) videoRepository.delete(video);
        return "영상 " + uuid + "는 성공적으로 삭제되었습니다.";
    }

    @GetMapping("/search")
    public List<Video> getCategory(@RequestParam Category category)
    {
        return videoRepository.findAllByCategory(category);
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
        ResourceRegion region = resourceRegion(video, headers);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM)).body(region);
    }

    private List<SimpleGrantedAuthority> getRoles(Member member)
    {
        return Arrays.stream(member.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws IOException
    {
        final long chunkSize = 500000L;
        long contentLength = video.contentLength();
        HttpRange httpRange = headers.getRange().stream().findFirst().get();

        if(httpRange != null)
        {
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Long.min(chunkSize, end - start + 1);
            return new ResourceRegion(video, start, rangeLength);
        } else
        {
            long rangeLength = Long.min(chunkSize, contentLength);
            return new ResourceRegion(video, 0, rangeLength);
        }
    }

    private class FileTypeError extends RuntimeException
    {
        public FileTypeError()
        {
            super("File Doesn't Fit to MP4, Try Again with MP4.");
        }
    }
}
