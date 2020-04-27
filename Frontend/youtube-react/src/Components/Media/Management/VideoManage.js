import React, {useEffect} from "react";
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import axios from "axios";
import {Link, useHistory} from "react-router-dom";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";

const useStyles = makeStyles({
    table: {
        maxWidth: 1000
    },
});

const VideoManage = (signed) =>
{
    const [data, setData] = React.useState([]);
    const [history] = React.useState(useHistory());
    const classes = useStyles();

    useEffect(() =>
    {
        axios.get("http://localhost:8080/token/verify/admin", {withCredentials: true})
            .then(() =>
            {
                loadData();
            })
            .catch(() =>
            {
                window.alert("You're Not Allowd to be here.");
                history.goBack();
            });
    }, [signed]);

    const loadData = async () =>
    {
        axios.get("http://localhost:8080/videos/all", { withCredentials: true })
            .then(res =>
            {
                setData(res.data);
            });
    };

    const del = async (e, uuid) =>
    {
        axios.delete("http://localhost:8080/videos/delete/"+uuid, { withCredentials: true })
            .then(() =>
            {
                window.alert("해당 영상을 삭제했습니다!");
                loadData();
            });
        e.preventDefault();
    };


    return (
        <div align={'center'}>
            <TableContainer component={Paper}>
            <Table className={classes.table} aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <TableCell>Category</TableCell>
                        <TableCell>Title</TableCell>
                        <TableCell align="center">UUID</TableCell>
                        <TableCell align="center">Uploader</TableCell>
                        <TableCell align="center">Date</TableCell>
                        <TableCell align="center">Delete</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        data.map((row) => (
                        <TableRow key={row.uuid}>
                            <TableCell component="th" scope="row">
                                {row.category.name}
                            </TableCell>
                            <TableCell align="center">{row.title}</TableCell>
                            <TableCell align="center">{row.uuid}</TableCell>
                            <TableCell align="center">{row.uploader}</TableCell>
                            <TableCell align="center">{row.date}</TableCell>
                            <TableCell align="center">
                                <Button size={"large"} onClick={e => del(e, row.uuid)} color="secondary">Delete</Button>
                            </TableCell>
                        </TableRow>
                        ))
                    }
                </TableBody>
            </Table>
        </TableContainer>
        </div>
    );
};

export default VideoManage;