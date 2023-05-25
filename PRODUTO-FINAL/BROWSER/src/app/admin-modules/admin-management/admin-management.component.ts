import { Component, OnInit} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
//import {MatIconModule} from '@angular/material/icon';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
//import { JitEvaluator } from '@angular/compiler';


@Component({
  selector: 'app-admin-management',
  templateUrl: './admin-management.component.html',
  styleUrls: ['./admin-management.component.scss']
})
export class AdminManagementComponent implements OnInit {

  

  //Validators...
  email = new FormControl('', [Validators.required]);
  name = new FormControl('',[Validators.required]);
  password = new FormControl('',[Validators.required]);
  lastName = new FormControl('',[Validators.required]);
  
  username = new FormControl('',[Validators.required]);
  street = new FormControl('',[Validators.required]);
  zipcode = new FormControl('',[Validators.required]);
  place = new FormControl('',[Validators.required]);
  country = new FormControl('',[Validators.required]);
  roleControl = new FormControl('',[Validators.required]);

  //Birthday:
  date = new FormControl(new Date());
  serializedDate = new FormControl((new Date()).toISOString());  

  selectedValue: string;
  hide = true;
  isRequestError:boolean = false;
  isRequestOK:boolean = false;
  constructor(private request: RequestService,private router : Router) { }

  ngOnInit(): void {}

  onSubmit(){
   const body={
      username: this.username.value.toString(),
			name: this.name.value.toString(),
			email: this.email.value.toString(),
			password: this.password.value.toString(),
			street: this.street.value.toString(),
			place: this.place.value.toString(),
			country: this.country.value.toString()
    }

    if(this.selectedValue == "BO"){
      this.boSubmit(body);
    }
    if(this.selectedValue == "BOM"){
      this.bomSubmit(body);
    }
    if(this.selectedValue == "BOP"){
      this.bopSubmit(body);
    }
  }

  boSubmit(body: { username: any; name: any; email: any; password: any; street: any; place: any; country: any; }) {
    this.request.boRegAdmin(body).subscribe(
      (data)=>{
        this.isRequestOK = true;
        alert("O Super Administador foi adicionado! The Super Admin was added successfully!");
        setTimeout(()=> this.isRequestOK = false,2500);
      },(err : HttpErrorResponse)=>{
        this.isRequestError = true;
        setTimeout( () => this.isRequestError = false , 2500 );
      }
    );
  }
  
  bomSubmit(body: { username: any; name: any; email: any; password: any; street: any; place: any; country: any; }) {
    this.request.bomRegAdmin(body).subscribe(
      (data)=>{
        this.isRequestOK = true;
        alert("O Moderador da Comunidade foi adicionado! The Community Admin was added successfully!");
        setTimeout(()=> this.isRequestOK = false,2500);
      },(err : HttpErrorResponse)=>{
        this.isRequestError = true;
        setTimeout( () => this.isRequestError = false , 2500 );
      });
  }
  
  bopSubmit(body: { username: any; name: any; email: any; password: any; street: any; place: any; country: any; }) {
    this.request.bopRegAdmin(body).subscribe(
      (data)=>{
        this.isRequestOK = true;
        alert("O Administrador do Mapa foi adicionado! The Map Admin was added successfully!");
        setTimeout(()=> this.isRequestOK = false,2500);
      },(err : HttpErrorResponse)=>{
        this.isRequestError = true;
        setTimeout( () => this.isRequestError = false , 2500 );
      }
      );
  }

  onCancel(){
    setTimeout( () => this.router.navigate(['/admin']) , 100 );
  }

}
