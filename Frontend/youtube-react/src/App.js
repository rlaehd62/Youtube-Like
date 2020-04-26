import React, {useEffect} from 'react';
import {BrowserRouter, Route} from "react-router-dom";
import BottomNav from "./Components/UI/BottomNav";
import UpperBar from "./Components/UI/UpperBar";
import VideoList from "./Components/Media/VideoList";
import VideoPage from "./Components/Media/VideoPage";
import Login from "./Components/Account/Login";
import FileUpload from "./Components/Media/FileUpload";
import axios from "axios";
import VideoManage from "./Components/Media/VideoManage";

function App() {

    const [signed, setSigned] = React.useState(false);
    const sign_in = React.useCallback((value) =>
    {
        setSigned(value);
    }, []);

    useEffect(() =>
    {
        axios.get("http://localhost:8080/token/verify", {withCredentials: true})
            .then(() =>
            {
                setSigned(true);
            })
            .catch(() =>
            {
                setSigned(false);
            });
    }, []);

  return (
      <BrowserRouter>
        <UpperBar signed={signed} sign_in={sign_in} />
        <Route exact path="/" render={(props) => <VideoList {...props} name="all" />}/>
        <Route exact path="/login" render={(props) => <Login {...props} sign_in={sign_in} />} />
        <Route exact path="/upload" component={FileUpload} />
        <Route exact path="/category/Bears" render={(props) => <VideoList {...props} name="WE_BARE_BEARS" />}/>
        <Route exact path="/category/Ratatouille" render={(props) => <VideoList {...props} name="RATATOUILLE" />}/>
        <Route exact path="/video/:uuid" component={VideoPage} />
        <Route exact path='/manage' component={VideoManage} />
        <BottomNav />
      </BrowserRouter>
  );
}

export default App;
