import React, {useEffect} from 'react';
import {Link, useHistory} from "react-router-dom";
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import axios from 'axios';
import Button from "@material-ui/core/Button";
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';

const useStyles = makeStyles((theme) => ({
    root: {
        '& > *': {
            margin: theme.spacing(1),
            width: '25ch',
        },
    },

    formControl: {
        margin: theme.spacing(1),
        minWidth: 120,
    },
    selectEmpty: {
        marginTop: theme.spacing(2),
    },
}));

const FileUpload = ({ category }) =>
{
    const classes = useStyles();
    const [history] = React.useState(useHistory());
    const [selected, select] = React.useState('');
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
                <div>
                    <FormControl className={classes.formControl}>
                        <InputLabel id="demo-simple-select-label">Category</InputLabel>
                        <Select
                            labelId="demo-simple-select-label"
                            id="demo-simple-select"
                            value={selected}
                            onChange={e => {select(e.target.value)}}>
                            {
                                category.map((row) => (
                                    <MenuItem value={row.name}>{row.name}</MenuItem>
                                ))
                            }
                        </Select>
                    </FormControl>
                </div>
                <TextField type='File' onChange={(e) => setFile(e.currentTarget.files[0])} label="Standard" />
            </form>

            <Button variant="contained" onClick={(event =>
            {
                const formData = new FormData();
                formData.append("title", title);
                formData.append("file", file);

                axios.post("http://localhost:8080/videos/upload/"+selected, formData, { withCredentials: true })
                    .then(() =>
                    {
                        history.push("/");
                    })
                    .catch((reason =>
                    {
                        setTitle('');
                        console.log(reason);
                    }));
            })} color="secondary">업로드</Button>
        </div>
    );

};

export default FileUpload;