package kid.youtube.Service;

import kid.youtube.Util.Name;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
public class UtilityService
{
    public ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws IOException
    {
        final long chunkSize = 500000L;
        long contentLength = video.contentLength();
        HttpRange httpRange = headers.getRange().stream().findFirst().get();

        if(Objects.nonNull(httpRange))
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

    public Cookie getCookie(String token)
    {
        Cookie cookie = new Cookie(Name.TOKEN, token);
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    public String findToken(HttpServletRequest request)
    {
        if(hasToken(request, false))
        {
            String token = request.getHeader(Name.TOKEN);
            Optional<String> op = Optional.of(token);

            op.orElseThrow(RuntimeException::new);
            return op.get();
        }
        else if(hasToken(request, true))
        {
            Optional<Cookie> op =
                    Arrays.stream(request.getCookies())
                            .filter(cookie -> cookie.getName().equals(Name.TOKEN))
                            .findAny();

            op.orElseThrow(RuntimeException::new);
            return op.get().getValue();
        }

        throw new RuntimeException("Nope!");
    }

    public boolean hasToken(HttpServletRequest request, boolean cookie_mode)
    {
        if(!cookie_mode)
        {
            String token = request.getHeader(Name.TOKEN);
            return Objects.nonNull(token);
        }

        if(Objects.isNull(request.getCookies())) return false;
        return Arrays.stream(request.getCookies())
                .map(Cookie::getName)
                .anyMatch(name -> name.equals(Name.TOKEN));
    }
}
