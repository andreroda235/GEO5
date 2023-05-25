import { Component, OnInit ,NgModule} from '@angular/core';
import { RequestService } from '../../services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { SimplebarAngularModule } from 'simplebar-angular';


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
    
    this.request.userRegist(body).subscribe((data : any)=>{
     console.log(data);
     localStorage.setItem('username',data.username);
     this.router.navigate(['/home']);
   },
   (err : HttpErrorResponse)=>{
     this.isRegError = true;
   });
  }else{
    console.log('error on logIn');
  }
 }
}
