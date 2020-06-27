import React, {useState} from "react";
import ReactPlayer from "react-player";

const VideoPage = (props) =>
{
    const[uuid] = useState(props.match.params.uuid);

    return (
        <div align={"center"} >
            <br /> <br />
            <ReactPlayer
                url={"http://localhost:8080/videos/streaming/" + uuid + ".mp4"}
                light={"http://localhost:8080/image/streaming/" + uuid + ".png"}
                playing
                controls
            />
            <br /> <br />
        </div>
    );
};

export default VideoPage;