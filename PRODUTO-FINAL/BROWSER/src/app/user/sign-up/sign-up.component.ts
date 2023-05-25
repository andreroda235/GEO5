//import { Component, OnInit ,NgModule} from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { RequestService } from '../../services/RequestService';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent implements OnInit {
  isRegError : boolean = false;

  constructor(private request:RequestService,private router:Router) { }

  ngOnInit(): void {
  }

  key = <any>'';
  
  OnSubmit(UserName,Name,Email,Password,Confirmation){
  
    if(Password===Confirmation){
    const body={
      username:UserName,
      name:Name,
      password:Password,
      email:Email
    }
    this.isRegError = true;
    this.request.userRegist(body).subscribe((data : any)=>{
     
      localStorage.setItem('username',body.username); 
      localStorage.setItem('password',body.password);

      setTimeout( () => this.router.navigate(['/user']) , 300 );

   },
   (err : HttpErrorResponse)=>{
     this.isRegError = true;
     setTimeout(()=>this.isRegError = false,1000);
   });
  }else{
    console.log('error on logIn');
  }
 }
}
