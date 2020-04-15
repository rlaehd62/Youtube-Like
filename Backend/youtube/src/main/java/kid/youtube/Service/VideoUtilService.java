package kid.youtube.Service;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.scale.AWTUtil;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

@Service
public class VideoUtilService
{
    private Logger log = Logger.getLogger(this.getClass().getName());

    // 영상의 전체 길이를 초 단위로 가져오는 메소드
    public void captureVideo(String video, Path root, String uuid)
    {
        try
        {
            SeekableByteChannel bc = NIOUtils.readableFileChannel(video);
            MP4Demuxer dm = MP4Demuxer.createMP4Demuxer(bc);
            DemuxerTrack vt = dm.getVideoTrack();

            double frameNumber = vt.getMeta().getTotalDuration() / 2;
            log.info(frameNumber + " / " + vt.getMeta().getTotalDuration() + " Seconds (영상 캡쳐)");
            printImage(new File(video), root, uuid, (int) frameNumber);
        }

        catch (Exception e)
        { e.printStackTrace(); }
    }

    // 영상 중 자신이 캡쳐할 부분의 초를 가져와서 이미지 파일을 생성하는 메소드.
    private void printImage(File file, Path root, String uuid, int sec) throws Exception
    {
        Picture picture = FrameGrab.getFrameAtSec(file, sec);
        BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
        ImageIO.write(bufferedImage, "png", new File(root.resolve(uuid + ".png").toString()));
    }
}
