import React, {useEffect} from 'react';
import {Link, useHistory} from "react-router-dom";
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import axios from 'axios';
import Button from "@material-ui/core/Button";

const useStyles = makeStyles((theme) => ({
    root: {
        '& > *': {
            margin: theme.spacing(1),
            width: '25ch',
        },
    },
}));

const FileUpload = () =>
{
    const classes = useStyles();
    const [history] = React.useState(useHistory());
    const [cate, setCate] = React.useState('');
    const [title, setTitle] = React.useState('');
    const [file, setFile] = React.useState([]);

    useEffect(() =>
    {
        axios.get("http://localhost:8080/token/verify/admin", {withCredentials: true})
            .catch(() =>
            {
                window.alert("You're Not Allowd to be here.");
                history.goBack();
            });
    }, []);

    return (
        <div align={"Center"}>
            <form className={classes.root} noValidate autoComplete="off">
                <TextField value={title} onChange={(e) => setTitle(e.target.value)} label="Title" />
                <TextField value={cate} onChange={(e) =>
                    setCate(e.target.value.toUpperCase().replace(" ", "_"))} label="Category" />
                <TextField type='File' onChange={(e) => setFile(e.currentTarget.files[0])} label="Standard" />
            </form>

            <Button variant="contained" onClick={(event =>
            {
                const formData = new FormData();
                formData.append("title", title);
                formData.append("file", file);

                axios.post("http://localhost:8080/videos/upload/"+cate, formData, { withCredentials: true })
                    .then(() =>
                    {

                        history.push("/");
                    })
                    .catch((reason =>
                    {
                        setTitle('');
                        setCate('');
                        console.log(reason);
                    }));
            })} color="secondary">업로드</Button>
        </div>
    );

};

export default FileUpload;