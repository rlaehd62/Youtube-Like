import React, {useState} from "react";
import ReactPlayer from "react-player";

const VideoPage = (props) =>
{
    const[uuid, setUUID] = useState(props.match.params.uuid);

    return (
        <div align={"center"}>
            <ReactPlayer
                url={"http://localhost:8080/videos/streaming/" + uuid + ".mp4"}
                light={"http://localhost:8080/image/streaming/" + uuid + ".png"}
                playing
                controls
            />
        </div>
    );
};

export default VideoPage;