import { Component, ViewChild, ElementRef} from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { UserUpdateData } from 'src/app/models/UserUpdateData';
import { RequestService } from 'src/app/services/RequestService';
import { User } from 'src/app/models/User';
import { UserUpNPData } from 'src/app/models/UserUpNPData';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent {
  @ViewChild('labelImport')
  labelImport: ElementRef;
  userInput = new UserUpdateData();
  isRequestError: boolean = false;

  //Validators...
  photoControl = new FormControl('', [Validators.required]);
  emailControl = new FormControl('', [Validators.required, Validators.email]);
  nameControl = new FormControl('',[Validators.required, this.checkName]);
  birthdayControl = new FormControl('',[Validators.required]);
  passControl = new FormControl('',[Validators.required, this.checkPassword]);
  streetControl = new FormControl('',[Validators.required, this.checkStreet]);
  zipcodeControl = new FormControl('',[Validators.required, this.checkZipCode]);
  placeControl = new FormControl('',[Validators.required, this.checkPlaceAndCountry]);
  countryControl = new FormControl('',[Validators.required, this.checkPlaceAndCountry]);

  constructor(private request: RequestService, private router : Router) { }

  // User Stored in the local storage.
  userI: User = JSON.parse(localStorage.getItem('userInfo'));
  imageProfile: File;

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

  onSubmit(){

    //Para ja' esta' a funcionar como debug para nos XD...
    //alert("Thanks for update you're info Data: "+ JSON.stringify(this.userInput));
   

    //Verificacao de campos introduzidos
    //Os campos que nao foram preenchidos ou seja nao existe atualizacao a fazer vamos buscar o valor 'a localstore...
    //Se existir alteracao guardamos na variavel da datastore para depois a actualizarmos
    if(this.userInput.user_name == null){
      this.userInput.user_name = this.userI.user_name;
    }
    else{
      this.userI.user_name = this.userInput.user_name;
    }

    if(this.userInput.user_email == null){
      this.userInput.user_email = this.userI.user_email;
    }
    else{
      this.userI.user_email = this.userInput.user_email;
    }

    if(this.userInput.user_street == null){
      this.userInput.user_street = this.userI.user_street;
    }
    else{
      this.userI.user_street = this.userInput.user_street;
    }

    if(this.userInput.user_place == null){
      this.userInput.user_place = this.userI.user_place;
    }
    else{
      this.userI.user_place = this.userInput.user_place;
    }

    if(this.userInput.user_country==null){
      this.userInput.user_country = this.userI.user_country;
    }
    else{
      this.userI.user_country = this.userInput.user_country;
    }

    if(this.userInput.user_birthday == null){
      this.userInput.user_birthday = this.userI.user_birthday;
    }
    else{
      this.userI.user_birthday = this.userInput.user_birthday;
    }

    if(this.userInput.user_zip_code == null){
      this.userInput.user_zip_code = this.userI.user_zip_code;
    }
    else{
      this.userI.user_zip_code = this.userInput.user_zip_code;
    }

    var body;
    //Escolha do servico para fazer update
    if(this.userInput.user_password == null){
      body = {
        name: this.userInput.user_name,
        email: this.userInput.user_email,        
        street: this.userInput.user_street,
        place: this.userInput.user_place,
        country: this.userInput.user_country,
        birthday: this.userInput.user_birthday,
        zipCode: this.userInput.user_zip_code,
      }
      console.log(body);
      this.request.upUserNPInfo(body).subscribe(
       (data)=> {
                  this.saveUpdate();
                  alert("Os seus dados foram actualizados com sucesso! Information updated successfully!");
                },(err : HttpErrorResponse)=>{
                  this.isRequestError = true;
                  setTimeout( () => this.isRequestError = false , 2500 );
          
          
                }
      );
    }
    //Need password update
    else{ 
      body = {
        name: this.userInput.user_name,
        email: this.userInput.user_email,
        password: this.userInput.user_password,
        street: this.userInput.user_street,
        place: this.userInput.user_place,
        country: this.userInput.user_country,
        birthday: this.userInput.user_birthday,
        zipCode: this.userInput.user_zip_code
      }     
      console.log(body);
      this.request.updateUserInfo(body).subscribe(
        (data)=>{
                  this.saveUpdate();
                  alert("Os seus dados foram actualizados com sucesso! Information updated successfully!");
                },(err : HttpErrorResponse)=>{
                  this.isRequestError = true;
                  setTimeout( () => this.isRequestError = false , 2500 );
          
          
                }
      );
    }

  }

  saveUpdate(){
    localStorage.setItem("userInfo", JSON.stringify(this.userI));    
    localStorage.setItem("ProfileTabIdx", "0");
    setTimeout( () => this.router.navigate(['/home']) , 0.01 );
    setTimeout( () => this.router.navigate(['/personAd']) , 100 );
  }

   //Implementar para guardar a imagem o utilizador no servidor...
   onFileChange(file: FileList){
    this.labelImport.nativeElement.innerText = Array.from(file)
    .map(f => f.name);
    this.imageProfile = file[0];
  }

  updatePhoto(){
    this.request.uploadPhoto(this.imageProfile,this.imageProfile.type).subscribe(res=>{ alert("All good enjoy you new photo!") },(err:HttpErrorResponse)=>{ console.log(err); alert("Something went wrong!"); }  );
  }




  //Para apagar (inativa) a conta do utilizador.

  onDelete(){
    this.request.deactivateMyAcc().subscribe(
      data=>{
        alert("A sua conta foi eliminada com sucesso! Esperamos que volte em breve! Your account was deleted successfully!");
      },(err: HttpErrorResponse)=>{console.log(err)}
    );

  }

  
  deleteUserInfo(){    
    localStorage.removeItem('tokenID');
    localStorage.removeItem('username');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('myDirections'); 
  }

  onCancel(){
    localStorage.setItem("ProfileTabIdx", "0");
    setTimeout( () => this.router.navigate(['/home']) , 0.01 );
    setTimeout( () => this.router.navigate(['/person']) , 100 );
  }

}
