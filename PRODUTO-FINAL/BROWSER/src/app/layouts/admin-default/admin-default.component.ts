import { Component, OnInit } from '@angular/core';
import { JwtHelperService } from "@auth0/angular-jwt";
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { User } from 'src/app/models/User';
import { username } from 'src/app/models/username';
import { HttpErrorResponse } from '@angular/common/http';
import { RequestService } from 'src/app/services/RequestService';


@Component({
  selector: 'app-admin-default',
  templateUrl: './admin-default.component.html',
  styleUrls: ['./admin-default.component.scss']
})
export class AdminDefaultComponent implements OnInit {

  sideBarOpen = true;
  l: string;
  userI:User;
  picSrc;

  constructor(private router:Router,public jwtHelper: JwtHelperService, public translate: TranslateService , private req: RequestService) { 
    translate.addLangs(['pt','en']);
  }

  ngOnInit(): void {
    this.router.navigate(['/admin']);

    this.l = localStorage.getItem('language');
    if(this.l==null){
      this.l='pt';
    } 

    localStorage.setItem('language',this.l);
    this.translate.setDefaultLang(this.l);

    const helper = new JwtHelperService();    
   // const decodedToken = helper.decodeToken(localStorage.getItem('tokenID'));
    //console.log(decodedToken);
    var date = new Date();
      if(this.jwtHelper.isTokenExpired(localStorage.getItem('tokenID'))){
      localStorage.removeItem('tokenID');
      this.router.navigate(['/signin']);
    }
   }

  sideBarToggler($event: any) {
    this.sideBarOpen = !this.sideBarOpen;
  }

  getPersoneInfo(){
    var us = localStorage.getItem("username");
   // console.log(us);
    const body = new username();
    body.username = us;
    //console.log(body);
    
    this.req.getUserInfo(body).subscribe(
  
      (data : any )=>{ console.log(data);
        
        this.userI.user_username = data.user_username;
        this.userI.user_name = data.user_name;
        this.userI.user_email = data.user_email;
        this.userI.user_role = data.user_role;
        if(data.user_street == ""){this.userI.user_street = "-";}
        else
          this.userI.user_street = data.user_street;
        if(data.user_place == ""){this.userI.user_place = "-";}
        else
          this.userI.user_place = data.user_place;
        if(data.user_country == ""){this.userI.user_country = "-";}
          else this.userI.user_country = data.user_country;
        if(data.user_birthday == ""){this.userI.user_birthday = "-";}
          else this.userI.user_birthday = data.user_birthday;
        if(data.user_zip_code == ""){this.userI.user_zip_code = "-";}
          else this.userI.user_zip_code = data.user_zip_code;
  
          this.savePersone(); 
      },

      (err : HttpErrorResponse)=>{
      //  this.isUInfoError = true;}

    });
  }

  loadPersone(){
    this.picSrc = localStorage.getItem("picSrc");
    
    if(this.picSrc == null){ this.picSrc = 'https://storage.cloud.google.com/apdc-geoproj.appspot.com/827cc967-51ac-488b-945b-62d400d98626'; }


    const st = localStorage.getItem("userInfo");
    if(st=="{}" || st==null){
      this.getPersoneInfo();
      setTimeout( ()=> 200);
    }
    else this.userI = JSON.parse(st);
  }

  savePersone(){
    localStorage.setItem("userInfo", JSON.stringify(this.userI));
  }

}
