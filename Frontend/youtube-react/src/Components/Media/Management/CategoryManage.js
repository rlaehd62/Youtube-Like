import React, {useEffect} from "react";
import {Link, useHistory} from "react-router-dom";
import axios from "axios";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";
import Input from "@material-ui/core/Input";

const useStyles = makeStyles({
    table: {
        maxWidth: 300
    },
});


const CategoryManage = ({ category, setCategory }) =>
{
    const [history] = React.useState(useHistory());
    const [value, setValue] = React.useState('');
    const classes = useStyles();

    useEffect(() =>
    {
        axios.get("http://localhost:8080/token/verify/admin", {withCredentials: true})
            .catch(() =>
            {
                window.alert("You're Not Allowd to be here.");
                history.goBack();
            });

    }, []);

    const add = async (e) =>
    {
        axios.post("http://localhost:8080/categories/generate?category="+value, { }, {  withCredentials: true })
            .then(() =>
            {
                window.alert("해당 카테고리를 추가했습니다!");
                setValue('');
                loadCategory();
            });
        e.preventDefault();
    };

    const del = async (e, name) =>
    {
        axios.delete("http://localhost:8080/categories/delete?category="+name, {  withCredentials: true })
            .then(() =>
            {
                window.alert("해당 카테고리를 삭제했습니다!");
                loadCategory();
            });

        e.preventDefault();
    };

    const handleValue = async (e) =>
    {
        setValue(e.target.value);
    };

    const loadCategory = async () =>
    {
        axios.get("http://localhost:8080/categories/list", { withCredentials: true })
            .then((response) =>
            {
                setCategory(response.data);
            });
    };

    return (
        <div align={'center'}>
            <br /> <br />
            <form className={classes.root} noValidate autoComplete="off">
                <Input placeholder="Category" value={value} onChange={handleValue} inputProps={{ 'aria-label': 'description' }} />
                &nbsp; &nbsp;
                <Button variant="contained" onClick={add} color="primary" href="#contained-buttons">추가</Button>
            </form>
            <br /> <br />
            <TableContainer component={Paper}>
                <Table className={classes.table} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell align="center">Category</TableCell>
                            <TableCell align="center">Delete</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {
                            category.map((row) => (
                                <TableRow key={row.name}>
                                    <TableCell align="center">{row.name}</TableCell>
                                    <TableCell align="center">
                                        <Button size={"large"} onClick={e => del(e, row.name)} color="secondary">Delete</Button>
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

export default CategoryManage;