import { Component, OnInit,  ViewEncapsulation } from '@angular/core';

import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';

import {User} from 'src/app/models/User';
import { username } from 'src/app/models/username';
import { MatTabChangeEvent } from '@angular/material/tabs';

@Component({
  selector: 'app-persone',
  templateUrl: './persone.component.html',
  styleUrls: ['./persone.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class PersoneComponent implements OnInit {

  isUInfoError: boolean = false;
  userI: User = new User();  
  index:any;

  constructor(private req: RequestService) { }

  ngOnInit(): void { 
    this.loadPersone(); 
    this.index = JSON.parse(localStorage.getItem("ProfileTabIdx"));
  }

  getPersoneInfo(){
    var us = localStorage.getItem("username");
    console.log(us);
    const body = new username();
    body.username = us;
    console.log(body);
    
    this.req.getUserInfo(body).subscribe(
  
      (data : any )=>{
        
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

          this.savePersone(); 
      },

      (err : HttpErrorResponse)=>{
        this.isUInfoError = true;}

    );
  }

  loadPersone(){
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

  tabChanged(tabChangeEvent: MatTabChangeEvent): void {
    localStorage.setItem("ProfileTabIdx", JSON.stringify(tabChangeEvent.index));
  }
  
}
