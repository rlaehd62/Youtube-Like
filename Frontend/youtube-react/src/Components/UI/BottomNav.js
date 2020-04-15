import React, {} from "react";
import { Link } from "react-router-dom";
import { makeStyles } from '@material-ui/core/styles';
import BottomNavigation from '@material-ui/core/BottomNavigation';
import BottomNavigationAction from '@material-ui/core/BottomNavigationAction';
import RestoreIcon from '@material-ui/icons/Restore';
import HomeIcon from '@material-ui/icons/Home';
import FavoriteIcon from '@material-ui/icons/Favorite';
import LocationOnIcon from '@material-ui/icons/LocationOn';

const useStyles = makeStyles({
    root: {
        width: "100%",
    },
});

const BottomNav = () =>
{
    const classes = useStyles();
    const [value, setValue] = React.useState(0);

    return (
        <BottomNavigation
            value={value}
            onChange={(event, newValue) =>
            {
                setValue(newValue);
            }}
            showLabels
            className={classes.root}
        >
            <BottomNavigationAction component={Link} to={"/"} label="Home" icon={<HomeIcon />} />
            <BottomNavigationAction component={Link} to={"/category/Bears"} label="We Bare Bears" icon={<LocationOnIcon />} />
            <BottomNavigationAction component={Link} to={"/category/Ratatouille"} label="Ratatouille" icon={<LocationOnIcon />} />
        </BottomNavigation>
    );
};

export default BottomNav;