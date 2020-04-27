import React, {useEffect} from "react";
import {Link, useHistory} from "react-router-dom";
import { makeStyles } from '@material-ui/core/styles';
import BottomNavigation from '@material-ui/core/BottomNavigation';
import BottomNavigationAction from '@material-ui/core/BottomNavigationAction';
import RestoreIcon from '@material-ui/icons/Restore';
import HomeIcon from '@material-ui/icons/Home';
import FavoriteIcon from '@material-ui/icons/Favorite';
import LocationOnIcon from '@material-ui/icons/LocationOn';
import axios from "axios";

const useStyles = makeStyles({
    root: {
        width: "100%",
    },
});

const BottomNav = ( { category, setList } ) =>
{
    const [value, setValue] = React.useState(0);
    const classes = useStyles();

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
            {
                category.map((row) => (
                        <BottomNavigationAction key={row.name} onClick={() =>
                        {

                        }} component={Link} to={"/category/"+row.name} label={row.name} icon={<HomeIcon />} />
                ))
            }
        </BottomNavigation>
    );
};

export default BottomNav;