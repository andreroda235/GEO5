import { Component} from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { UserUpNPData } from 'src/app/models/UserUpNPData';
import { User } from 'src/app/models/User';
import { Validators, FormControl } from '@angular/forms';
import { RequestService } from 'src/app/services/RequestService';
import { UserUpdateData } from 'src/app/models/UserUpdateData';

@Component({
  selector: 'app-settings',
  templateUrl: './admin-settings.component.html',
  styleUrls: ['./admin-settings.component.css']
})
export class AdminSettingsComponent {

  userInput = new UserUpdateData();
  isRequestError: boolean = false;

  //Validators...
  emailControl = new FormControl('', [Validators.required, Validators.email]);
  nameControl = new FormControl('',[Validators.required, this.checkName]);
  birthdayControl = new FormControl('',[Validators.required]);
  passControl = new FormControl('',[Validators.required, this.checkPassword]);
  streetControl = new FormControl('',[Validators.required, this.checkStreet]);
  zipcodeControl = new FormControl('',[Validators.required, this.checkZipCode]);
  placeControl = new FormControl('',[Validators.required, this.checkPlaceAndCountry]);
  countryControl = new FormControl('',[Validators.required, this.checkPlaceAndCountry]);

  constructor(private request: RequestService) { }

  // User Stored in the local storage.
  userI: User = JSON.parse(localStorage.getItem('userInfo'));
  userUpNoPass = new UserUpNPData();

  checkName(control){
    let enteredName = control.value;
    let nameCheck = /^(?=.*[a-z])(?=.{3,})/;
    return (!nameCheck.test(enteredName) && enteredName) ? { 'requirements': true } : null;
  }
  checkPassword(control) {
    let enteredPassword = control.value;
    let passwordCheck = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})/;
    return (!passwordCheck.test(enteredPassword) && enteredPassword) ? { 'requirements': true } : null;
  }
  checkStreet(control){
    let enteredStreet = control.value;
    let streetCheck = /^(?=.*[a-z])(?=.*[A-Z])(?=[0-9]{0,10})(?=.{8,45})/;
    return (!streetCheck.test(enteredStreet) && enteredStreet) ? { 'requirements': true } : null;
  }
  checkZipCode(control){
    let enteredzipcode = control.value;
    let zipcodeCheck = /^(?=[0-9]{4})(?=[0-9]{3})(?=.{8,})/;
    return (!zipcodeCheck.test(enteredzipcode) && enteredzipcode) ? { 'requirements': true } : null;
  }

  checkPlaceAndCountry(control){
    let enteredPlace = control.value;
    let placeCheck = /^(?=.*[a-z])(?=.*[A-Z])(?=.{3,25})/;
    return (!placeCheck.test(enteredPlace) && enteredPlace) ? { 'requirements': true } : null;
  }

  getErrorMessage(){

    if (this.emailControl.hasError('email')) {
      return 'Your email update is not valid.';
    }
    if(this.nameControl.hasError('requirements')){
      return 'Your name update is not valid. Your name must have at least 3 letters.';
    }
    if(this.passControl.hasError('requirements')){
      return 'Password needs to be at least eight characters, one uppercase letter and one number.';
    }
    if(this.streetControl.hasError('requirements')){
      return 'Your street update is not valid. The street name must have at least eight characters and one uppercase letter.';
    }
    if(this.zipcodeControl.hasError('requirements')){
      return 'Your zip-code update is not valid. Format valid xxxx-xxx (just numbers)';
    }
    if(this.placeControl.hasError('requirements')){
      return 'Your City update is not valid. The City must have a name between 3-25 characters and one uppercase letter';
    }
    if(this.countryControl.hasError('requirements')){
      return 'Your Country update is not valid. The Country must have a name between 3-25 characters and one uppercase letter';
    }
  }
  onSubmit(){

    //Para ja' esta' a funcionar como debug para nos XD...
    alert("Thanks for update you're info Data: "+ JSON.stringify(this.userInput));

    //Verificacao de campos introduzidos
    //Os campos que nao foram preenchidos ou seja nao existe atualizacao a fazer vamos buscar o valor 'a localstore...
    //Se existir alteracao guardamos na variavel da datastore para depois a actualizarmos
    if(this.userInput.user_name == ""){
      this.userInput.user_name = this.userI.user_name;
      this.userUpNoPass.user_name = this.userI.user_name;
    }
    else{
      this.userI.user_name = this.userInput.user_name;
      this.userUpNoPass.user_name = this.userInput.user_name;
    }

    if(this.userInput.user_email == ""){
      this.userInput.user_email = this.userI.user_email;
      this.userUpNoPass.user_email = this.userI.user_email;
    }
    else{
      this.userI.user_email = this.userInput.user_email;
      this.userUpNoPass.user_email = this.userInput.user_email;
    }

    if(this.userInput.user_street == ""){
      this.userInput.user_street = this.userI.user_street;
      this.userUpNoPass.user_street = this.userI.user_street;
    }
    else{
      this.userI.user_street = this.userInput.user_street;
      this.userUpNoPass.user_street = this.userInput.user_street;
    }

    if(this.userInput.user_place == ""){
      this.userInput.user_place = this.userI.user_place;
      this.userUpNoPass.user_place = this.userI.user_place;
    }
    else{
      this.userI.user_place = this.userInput.user_place;
      this.userUpNoPass.user_place = this.userInput.user_place;
    }

    if(this.userInput.user_country==""){
      this.userInput.user_country = this.userI.user_country;
      this.userUpNoPass.user_country = this.userI.user_country;
    }
    else{
      this.userI.user_country = this.userInput.user_country;
      this.userUpNoPass.user_country = this.userInput.user_country;
    }

    //Escolha do servico para fazer update
    if(this.userInput.user_password == ""){
      this.request.upUserNPInfo(this.userUpNoPass).subscribe(
       (data)=> {
                  this.saveUpdate();
                },(err : HttpErrorResponse)=>{
                  this.isRequestError = true;
                  setTimeout( () => this.isRequestError = false , 2500 );
          
          
                }
      );
    }
    //Need password update
    else{      
      this.request.updateUserInfo(this.userInput).subscribe(
        (data)=>{
                  this.saveUpdate();
                },(err : HttpErrorResponse)=>{
                  this.isRequestError = true;
                  setTimeout( () => this.isRequestError = false , 2500 );
          
          
                }
      );
    }

  }

  saveUpdate(){
    localStorage.setItem("userInfo", JSON.stringify(this.userI));    
  }

}
