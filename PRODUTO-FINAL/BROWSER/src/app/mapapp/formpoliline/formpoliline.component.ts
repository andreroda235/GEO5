import {Component, ElementRef, ViewChild, Output, EventEmitter} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import { promise } from 'protractor';
import { RequestService } from 'src/app/services/RequestService';


@Component({
  selector: 'app-formpoliline',
  templateUrl: './formpoliline.component.html',
  styleUrls: ['./formpoliline.component.css']
})
export class FormpolilineComponent{
  @Output() newImagesAdded: EventEmitter<FileList> = new EventEmitter();
  @ViewChild('labelImport')
  labelImport: ElementRef;
  imageSend = false;
  formImport: FormGroup;
  filesToUpload: FileList = null;
  filetoUpload: File = null;
   
  constructor(private router:Router,private req:RequestService) {
    this.formImport = new FormGroup({
      importFile: new FormControl('', Validators.required)
    });
  }

  onFileChange(files: FileList) {
    this.labelImport.nativeElement.innerText = Array.from(files)
      .map(f => f.name)
      .join(', ');
    this.filesToUpload = files;
  }

  imageToShow:any;
  public imagePath;
  imgURL: any;
  images:any[];

  importImages():void{

    var reader = new FileReader();
    this.imagePath = this.filesToUpload;

    reader.readAsDataURL(this.filesToUpload[0]); 
    reader.onload = (_event) => { 
      this.imgURL = reader.result; 
    }

    const arrayOfBase64 = fileListToBase64(this.filesToUpload);
   
    arrayOfBase64.then(data => {
      this.images = data;
      this.imageSend = true;
      this.newImagesAdded.emit(this.filesToUpload);
      
  });
  }
}
  export async function fileListToBase64(fileList) {
    // create function which return resolved promise
    // with data:base64 string
    function getBase64(file) {
      const reader = new FileReader()
      return new Promise(resolve => {
        reader.onload = ev => {
          resolve(ev.target.result)
        }
        reader.readAsDataURL(file)
      })
    }
    // here will be array of promisified functions
    const promises = []
  
    // loop through fileList with for loop
    for (let i = 0; i < fileList.length; i++) {
      promises.push(getBase64(fileList[i]))
    }
  
    // array with base64 strings
    return await Promise.all(promises)
  }


  /*
  import(): void {
    var ss = '';
 
    const reader = new FileReader();
    reader.onload = function(event) {
      ss = event.target.result.toString();
      var start = ss.lastIndexOf("<coordinates>");
      var end = ss.lastIndexOf("</coordinates>");
      var smore = ss.slice(start,end);
      var startn = smore.indexOf("-");
      var allmixup = smore.slice(startn);
      var ordered = [];
      
      ordered = allmixup.split('\n');
      var orderedWtoSpace = [];
      ordered.forEach(element => { 
          orderedWtoSpace.push(element.trim().split(","));
      });
      orderedWtoSpace.pop();
      localStorage.setItem("mySpecialDir",JSON.stringify(orderedWtoSpace));
      
    };
    
  //  reader.readAsText(this.fileToUpload);
    
    this.newDirectionAdded.emit(true);

        
  }
*/
