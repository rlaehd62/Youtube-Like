import React, {useState, useEffect} from "react";
import { makeStyles } from '@material-ui/core/styles';
import { Link } from "react-router-dom";
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import CardMedia from '@material-ui/core/CardMedia';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import axios from 'axios';

const useStyles = makeStyles({
    root: {
        maxWidth: 345,
        margin: 20,
    },
    media: {
        height: 140,
    },
});

const VideoList = (props) =>
{
    const[list, setList] = useState([]);
    const classes = useStyles();

    useEffect(() =>
    {
        loadVides(props.match.params.name);
    }, [props.match.params.name]);

    const loadVides = (name) =>
    {
        if(props.match.params.name == undefined)
        {
            axios.get("http://localhost:8080/videos/all", { withCredentials: true })
                .then(res =>
                {
                    const data = res.data;
                    setList(data);
                });
        } else
        {
            axios.get("http://localhost:8080/videos/search?category="+name, { withCredentials: true })
                .then(res =>
                {
                    const data = res.data;
                    setList(data);
                });
        }
    };

    const getVideoCard = (video) =>
    {
        return (
            <Card key={video.uuid} className={classes.root}>
                <CardActionArea>
                    <CardMedia
                        className={classes.media}
                        image={"http://localhost:8080/image/streaming/" + video.uuid + ".png"}
                        title={video.title}
                    />
                    <CardContent>
                        <Typography gutterBottom variant="h5" component="h2">
                            {video.title}
                        </Typography>
                        <Typography variant="body2" color="textSecondary" component="p">
                            Uploader: {video.uploader}
                        </Typography>
                    </CardContent>
                </CardActionArea>
                <CardActions>
                    <Button size="small" color="primary" to={"/video/" + video.uuid} component={Link}>
                        WATCH
                    </Button>
                </CardActions>
            </Card>
        );
    };

    return (
        <div align={"center"} style={
            {
                width: "100%",
                display: "flex",
                justifyContent: "center"
            }}>
            { list.map((video, index) => getVideoCard(video)) }
        </div>
    );
};

export default VideoList;