import React, {useEffect, useState} from 'react';
import {BrowserRouter, Route} from "react-router-dom";
import BottomNav from "./Components/UI/BottomNav";
import UpperBar from "./Components/UI/UpperBar";
import VideoList from "./Components/Media/VideoList";
import VideoPage from "./Components/Media/VideoPage";
import Login from "./Components/Account/Login";
import FileUpload from "./Components/Media/Management/FileUpload";
import axios from "axios";
import VideoManage from "./Components/Media/Management/VideoManage";
import CategoryManage from "./Components/Media/Management/CategoryManage";

function App() {

    const [signed, setSigned] = React.useState(false);
    const [category, setCategory] = React.useState([]);

    const loadCategory = async () =>
    {
        axios.get("http://localhost:8080/categories/list", { withCredentials: true })
            .then((response) =>
            {
                setCategory(response.data);
            });
    };

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

        loadCategory();
    }, []);

  return (
      <BrowserRouter>
        <UpperBar signed={signed} sign_in={sign_in} />
        <Route exact path="/" render={(props) => <VideoList {...props} />} />
        <Route path="/category/:name" render={(props) => <VideoList {...props} />} />
        <Route exact path="/manage/category" render={(props) => <CategoryManage {...props} category={category} setCategory={setCategory} /> } />
        <Route exact path="/login" render={(props) => <Login {...props} sign_in={sign_in} />} />
        <Route exact path="/upload" render={(props) => <FileUpload {...props} category={category} />} />
        <Route path="/video/:uuid" component={VideoPage} />
        <Route exact path='/manage' component={VideoManage} />
        <BottomNav category={category} />
      </BrowserRouter>
  );
}

export default App;
