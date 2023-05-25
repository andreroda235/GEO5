import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {NgForm} from '@angular/forms';
import { randomImages } from '../mapapp/mapapp.component'; //lixo
import { Direction } from '../models/Direction';
import { Data } from '@angular/router';
import { Url } from 'url';
import { JwtHelperService } from '@auth0/angular-jwt';
const httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }
  
  @Injectable({
    providedIn: 'root'
  })
export class RequestService {

  url = 'https://picsum.photos/v2/list?page=2&limit=1'; //lixo
   
  getTodos():Observable<randomImages[]> { //lixo
    return this.http.get<randomImages[]>(`${this.url}`);
    }
      
    
  isLoggedIn() {
      return !!this.getJwtToken();
  }
  

    todosUrl:string = '';

    todosLimit = '?_limit=5';
    forecast:string = '';

    helper
    constructor(private http:HttpClient,public jwtHelper: JwtHelperService) {
       this.helper = new JwtHelperService(); 
     }

     addToFav = '/rest/route/submit';
     addToFovorites(direction:Direction):Observable<JSON>{
       
        const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.addToFav}`,direction,httpOption);
     }
     addRoutePho="/rest/storage/upload/route/"; //{routeid}"
     addRoutePhoto(id,body,type){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': type,
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.addRoutePho}${id}`,body,httpOption);
     }
     getRouteP="/rest/route/";
     getRoutePhotos(id){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.getRouteP}${id}${"/pictures"}`,'',httpOption);
     }


     ///////////////GEOSPOT////////////////////////
    geoSC = "/rest/geoSpot/submit";
    submitGeoSpot(body){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.geoSC}`,body,httpOption);
    }

    getGeo = "/rest/geoSpot/listActive";
    getActiveGeoSpots(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.getGeo}`,'',httpOption);
    }

    geoImage="/rest/storage/upload/geoSpot/";
    addGeoSpotPhoto(nome,body,type){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': type,
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.geoImage}${nome}`,body,httpOption);
     }
     
     getgeoSpotP="/rest/geoSpot/"  //{geoSpotName}/pictures"
     getgeoSpotPhotos(nome){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.getgeoSpotP}${nome}${'/pictures'}`,'',httpOption);
     }

     geoDelComm="/rest/communityModerator/makeGeoSpotCommentInactive/"
     deactivateGeoComment(id){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.geoDelComm}${id}`,'',httpOption);
     }

///////////////////////////InfoNotices/////////////////////
    infoResPath = "/rest/info/submit";
    submitInfoRes(body){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.infoResPath}`,body,httpOption);
    }

    infoResPhotos = "/rest/storage/upload/info/"//infoname
    addInfoResPhoto(nome,body,type){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': type,
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.infoResPhotos}${nome}`,body,httpOption);
    }

    getInfoRes = "/rest/info/listActive"
    getActiveInfoRes(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.getInfoRes}`,'',httpOption);
    }

    getInfoPhoto = "/rest/info/";//nome/pictures
    getInfosPhotos(nome){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.getInfoPhoto}${nome}${'/pictures'}`,'',httpOption);
    }

///////////////////////////////Quizzes///////////////////
    submitQuizzUri="/rest/quizz/submit";
    submitQuizze(body){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.submitQuizzUri}`,body,httpOption);
    }


///////////////////////////////tabelas//////////////
    allUsersGet="/rest/user/listAllUsers";
    getAllUsers() {
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.allUsersGet}`,'',httpOption);
    }

    allAdminGet="/rest/backOffice/listActiveAdmins"
    getAllActiveAdmin(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this. allAdminGet}`,'',httpOption);
    }

///////////////////////////community//////////////
    deactivateUri = "/rest/communityModerator/makeUserInactive";
    deactivateAccCommunity(body){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.deactivateUri}`,body,httpOption);
    } 

    activateURI = "/rest/communityModerator/makeUserActive";
    activateAccCommunity(body){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.activateURI}`,body,httpOption);
    }


///////////////////////deactivate my acc/////////////
    deactMyUri="/rest/user/makeAccountInactive"
    deactivateMyAcc(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.deactMyUri}`,'',httpOption);
    }
///////////////delete////////////////
    inactiveU="/rest/user/listInactive"
    getAllInactiveUsers(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.inactiveU}`,'',httpOption);
    }


    //////////////////////////GET QUIZZ////////////////////
    getQuizzUri="/rest/quizz/listActive";
    getAllQuizzes(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.getQuizzUri}`,'',httpOption);
    }



    /////////////////////////Get active geo Cooments/////////////////////
    getgeoCommentsA="/rest/comment/geoSpot/listActive";
    getAllActiveCommentGeo(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.getgeoCommentsA}`,'',httpOption);
    }


    ////////////////Get active route Comments////////////
    getRouteCommentA="/rest/comment/route/listActive"
    getAllActiveCommentRoute(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<any[]>(`${this.getRouteCommentA}`,'',httpOption);
    }

    dectRouteComm="/rest/communityModerator/makeRouteCommentInactive/"
    deactivateRouteComment(id){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.dectRouteComm}${id}`,'',httpOption);
    }


    //////////////Change role////////////
    changeAdminRoleU = "/rest/backOffice/updateRole"
    changeAdminRoleTo(body){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.changeAdminRoleU}`,body,httpOption);
    }



///////////////////////////Deleteincactive users//////////////
    deleteAllI="/rest/delete/inactiveUsers";
    deleteAllUserI(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.deleteAllI}`,'',httpOption);
    }


    getImagesFromURL(uri){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.get(uri);
    }

    userLogs='/rest/login/user';
    getlogs( me : any ):Observable<string[]>{
        return this.http.post<string[]>(`${this.todosUrl}${this.userLogs}`,me,httpOptions);
    }

    userlogin='/rest/login';
    userAuthentication(body):Observable<JSON>{   
      return this.http.post<JSON>(`${this.todosUrl}${this.userlogin}`,body,httpOptions);
    }

    refToken='/rest/user/refreshToken';
    refreshToken(){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.getUser}`,'',httpOption);
    }

    userlogout='/rest/logout';
    logOutUser(token){
      return this.http.post<JSON>(`${this.todosUrl}${this.userlogout}`,token,httpOptions);
    }

    userReg='/rest/register/confirm';
    userRegist(body):Observable<JSON>{ 
      return this.http.post<JSON>(`${this.todosUrl}${this.userReg}`,body,httpOptions);
    }
    activateAcc='/rest/user/activateAccount';
    userActAcc(body):Observable<JSON>{
      return this.http.post<JSON>(`${this.todosUrl}${this.activateAcc}`,body,httpOptions);
    }

    getUser = '/rest/user/get';
    getUserInfo(body):Observable<JSON>{
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.getUser}`,body,httpOption);
    }

    updateUser = '/rest/update';
    updateUserInfo(body):Observable<JSON>{
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.updateUser}`,body,httpOption);
    }

    upUserNP = 'rest/update/v2';
    upUserNPInfo(body):Observable<JSON>{
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.upUserNP}`,body,httpOption);
    }
    
    getJwtToken() {
      return localStorage.getItem('tokenID');
    }

    getAdminConf(){
      var u = localStorage.getItem('userInfo');
      if(u!=null){
        if(JSON.parse(u).user_role != 'User'){
          return true;
        }
      }
      return false;
    }


    getCams='/rest/route/user';
    getmyCams():Observable<JSON>{
      const uname = localStorage.getItem('username');
      const body={
        "username":uname
      }
      console.log(uname);
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.getCams}`,body,httpOption);
    }
    getCamsWSearch = '/rest/route/searchActive';
    searchWithKeyword(body):Observable<JSON>{
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.getCamsWSearch}`,body,httpOption);
    }

    getInfosOfPoint(urlSpls):Observable<Blob>{
      return this.http.get( urlSpls , { responseType: 'blob' });
    }
    
    admintypebo = '/rest/backOffice/addBO';
    boRegAdmin(body){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.admintypebo}`,body,httpOption);
    }

    admintypebom = '/rest/backOffice/addBOM';
    bomRegAdmin(body){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.admintypebom}`,body,httpOption);
    }
    admintypebop = '/rest/backOffice/addBOP';
    bopRegAdmin(body){
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'token': `${localStorage.getItem('tokenID')}`
        })
      }
      return this.http.post<JSON>(`${this.admintypebop}`,body,httpOption);
    }

    simUpload='/rest/storage/upload'
    simplUpload(body){
      const decodedToken = this.helper.decodeToken(localStorage.getItem('tokenID'));
     
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type':'image/jpeg',
          'token': `${localStorage.getItem('tokenID')}`
      })
    }
    return this.http.post<JSON>(`${this.simUpload}`,body);
    }
    
    
    uploadPho = '/rest/storage/upload/user/'
    uploadPhoto(body,type){
      const decodedToken = this.helper.decodeToken(localStorage.getItem('tokenID'));
     
      const httpOption = {
        headers: new HttpHeaders({
          'Content-Type': type,
          'token': `${localStorage.getItem('tokenID')}`
      })
    }
    return this.http.post<JSON>(`${this.uploadPho}${decodedToken.token.username}`,body,httpOption);
    }

    userPict='/rest/user/'
    getUserProfPic(){
      const decodedToken = this.helper.decodeToken(localStorage.getItem('tokenID'));
     
      const httpOption = {
        headers: new HttpHeaders({
          'token': `${localStorage.getItem('tokenID')}`
      })
      
    }
   // console.log(`${this.userPict}${decodedToken.token.username}${'/picture'}`);
    return this.http.post<JSON>(`${this.userPict}${decodedToken.token.username}${'/picture'}`,'',httpOption);
    }


}