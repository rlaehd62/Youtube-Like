import React, {useEffect} from "react";
import {Link} from "react-router-dom";
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import axios from 'axios';

const useStyles = makeStyles(theme => ({
    root: {
        flexGrow: 1,
    },
    menuButton: {
        marginRight: theme.spacing(2),
    },
    title: {
        flexGrow: 1,
    },
}));


const UpperBar = ({signed, sign_in}) =>
{
    const classes = useStyles();
    const [admin, setAdmin] = React.useState(false);

    useEffect(() =>
    {
        check_admin();
    }, [signed]);

    const check_admin = async () =>
    {
        axios.get("http://localhost:8080/token/verify/admin", {withCredentials: true})
            .then(() =>
            {
                setAdmin(true);
            })
            .catch(() =>
            {
                setAdmin(false);
            });
    };

    const logout = async (e) =>
    {
        axios.delete("http://localhost:8080/token/expires", { withCredentials: true})
            .then(() =>
            {
                sign_in(false);
            });

        e.preventDefault();
    };

    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" className={classes.title}>Youtube</Typography>
                {
                    admin ?
                        <Button size={"large"} to="/upload" component={Link} color="inherit">업로드</Button>
                        : ''
                }

                {
                    admin ?
                    <Button size={"large"} to="/manage" component={Link} color="inherit">영상 관리</Button>
                        : ''
                }

                {
                    signed ?
                        <Button size={"large"} onClick={e => logout(e)} to="/" component={Link} color="inherit">로그아웃</Button>
                        :
                        <Button size={"large"} to="/login" component={Link} color="inherit">로그인</Button>
                }
            </Toolbar>
        </AppBar>
    );
};

export default UpperBar;