import React from 'react';
import {BrowserRouter, Route} from "react-router-dom";
import BottomNav from "./Components/UI/BottomNav";
import UpperBar from "./Components/UI/UpperBar";
import VideoList from "./Components/Media/VideoList";
import VideoPage from "./Components/Media/VideoPage";
import Login from "./Components/Account/Login";
import FileUpload from "./Components/Media/FileUpload";

function App() {
  return (
      <BrowserRouter>
        <UpperBar />
        <Route exact path="/" render={(props) => <VideoList {...props} name="all" />}/>
        <Route exact path="/login" component={Login} />
        <Route exact path="/upload" component={FileUpload} />
        <Route exact path="/category/Bears" render={(props) => <VideoList {...props} name="WE_BARE_BEARS" />}/>
        <Route exact path="/category/Ratatouille" render={(props) => <VideoList {...props} name="RATATOUILLE" />}/>
        <Route exact path="/video/:uuid" component={VideoPage} />
        <BottomNav />
      </BrowserRouter>
  );
}

export default App;
