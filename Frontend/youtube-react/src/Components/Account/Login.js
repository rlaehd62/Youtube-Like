import React, {useEffect} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Input from '@material-ui/core/Input';
import Button from '@material-ui/core/Button';
import axios from 'axios';
import {Link} from "react-router-dom";

const useStyles = makeStyles((theme) => ({
    root: {
        '& > *': {
            margin: theme.spacing(1),
        },
    },
}));

const Login = (props) =>
{
    const classes = useStyles();
    const [id, setID] = React.useState('');
    const [pw, setPW] = React.useState('');

    const login = async () =>
    {
        axios.get("http://localhost:8080/token/generate?id="+id+"&pw="+pw, {withCredentials: true})
            .then(() =>
            {
                console.log("Login 성공!");
            });
    };

    return (
        <div align={"center"}>
            <form className={classes.root} noValidate autoComplete="off">
                <Input placeholder="ID" value={id} onChange={event => setID(event.target.value)} inputProps={{ 'aria-label': 'description' }} />
                <Input type="Password" placeholder="PW" value={pw} onChange={event => setPW(event.target.value)} inputProps={{ 'aria-label': 'description' }} />
                <Button variant="contained" color="primary" href="#contained-buttons" onClick={login} to={"/"} component={Link}>
                    로그인
                </Button>
            </form>
        </div>
    );
};

export default Login;