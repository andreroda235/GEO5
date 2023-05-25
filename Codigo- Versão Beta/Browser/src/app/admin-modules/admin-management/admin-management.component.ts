import { Component, OnInit } from '@angular/core';
import {FormControl, Validators} from '@angular/forms';

@Component({
  selector: 'app-admin-management',
  templateUrl: './admin-management.component.html',
  styleUrls: ['./admin-management.component.scss']
})
export class AdminManagementComponent implements OnInit {

  //Validators...
  email = new FormControl('', [Validators.required, Validators.email]);
  name = new FormControl('',[Validators.required]);
  lastName = new FormControl('',[Validators.required]);
  username = new FormControl('',[Validators.required]);
  street = new FormControl('',[Validators.required]);
  zipcode = new FormControl('',[Validators.required]);
  place = new FormControl('',[Validators.required]);
  city = new FormControl('',[Validators.required]);
  country = new FormControl('',[Validators.required]);
  roleControl = new FormControl('',[Validators.required]);

  //Birthday:
  date = new FormControl(new Date());
  serializedDate = new FormControl((new Date()).toISOString());  

  selectedValue: string;

  constructor() { }

  ngOnInit(): void {  }

  getEmailErrorMessage() {
    if (this.email.hasError('required')) {
      return 'You must enter a value';
    }

    return this.email.hasError('email') ? 'Not a valid email' : '';
  }

  getNameErrorMessage() {
    if(this.name.hasError('required')){
      return 'You must enter a name (First and Last)';
    }
    //return this.name.hasError('name') ? 'Not a valid name (First and Last)' : '';
  }

  getUNErrorMessage(){
    if(this.username.hasError('required')){
      return 'You must enter a username';
    }
    //return this.username.hasError('username') ? 'Not a valid username' : '';
  }

  getStreetErrorMessage(){
    if(this.street.hasError('required')){
      return 'You must enter a street';
    }
    return this.street.hasError('street') ? 'Not a valid street' : '';
  }

  getZipCErrorMessage(){
    if(this.zipcode.hasError('required')){
      return 'You must enter a zip-code';
    }
    return this.zipcode.hasError('zipcode') ? 'Not a valid zip-code' : '';
  }

  getPlaceErrorMessage(){
    if(this.place.hasError('required')){
      return 'You must enter a place';
    }
    return this.place.hasError('place') ? 'Not a valid place' : '';
  }

  getCityErrorMessage(){
    if(this.city.hasError('required')){
      return 'You must enter a city';
    }
    return this.place.hasError('city') ? 'Not a valid city' : '';
  }

  getCountryErrorMessage(){
    if(this.country.hasError('required')){
      return 'You must enter a country';
    }
    return this.country.hasError('country') ? 'Not a valid country' : '';

  }
}
