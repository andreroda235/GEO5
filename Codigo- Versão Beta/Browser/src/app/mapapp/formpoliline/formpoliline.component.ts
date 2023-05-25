import {Component, ElementRef, ViewChild, Output, EventEmitter} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import { Router } from '@angular/router';



@Component({
  selector: 'app-formpoliline',
  templateUrl: './formpoliline.component.html',
  styleUrls: ['./formpoliline.component.css']
})
export class FormpolilineComponent{
  @Output() newDirectionAdded: EventEmitter<boolean> = new EventEmitter();
  @ViewChild('labelImport')
  labelImport: ElementRef;

  formImport: FormGroup;
  fileToUpload: File = null;

  constructor(private router:Router) {
    this.formImport = new FormGroup({
      importFile: new FormControl('', Validators.required)
    });
  }

  onFileChange(files: FileList) {
    this.labelImport.nativeElement.innerText = Array.from(files)
      .map(f => f.name)
      .join(', ');
    this.fileToUpload = files.item(0);
  }


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
    
    reader.readAsText(this.fileToUpload);
    
    this.newDirectionAdded.emit(true);

        
  }

}