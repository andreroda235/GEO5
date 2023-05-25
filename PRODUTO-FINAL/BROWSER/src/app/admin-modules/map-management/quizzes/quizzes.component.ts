import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-quizzes',
  templateUrl: './quizzes.component.html',
  styleUrls: ['./quizzes.component.css']
})
export class QuizzesComponent implements OnInit {

  dynamicForm: FormGroup;
  submitted = false;
  quizzTitle: String;
  quizzIntro: String;
  quizzKW: String;

  constructor(private formBuilder: FormBuilder, private req: RequestService,private router: Router) { }

  ngOnInit(): void {
    this.dynamicForm = this.formBuilder.group({
      numberOfQuestions: ['', Validators.required],
      quizzTitle:['',Validators.required],
      quizzIntro : ['', Validators.required],
      quizzKW : ['', Validators.required],
      questions: new FormArray([])
    });
  }

  // convenience getters for easy access to form fields
  get f() { return this.dynamicForm.controls; }
  get q() { return this.f.questions as FormArray; }

  onChangeQuestions(e) {
    const numberOfQuestions = e.target.value || 0;
    if (this.q.length < numberOfQuestions) {
        for (let i = this.q.length; i < numberOfQuestions; i++) {
            this.q.push(this.formBuilder.group({
              number:(i+1).toString(),
              question: ['', Validators.required],
              rightAnswer: ['', Validators.required],
              wrongAnswer1: ['', Validators.required],
              wrongAnswer2: ['', Validators.required],
              wrongAnswer3: ['', Validators.required],
            }));
        }
    } else {
        for (let i = this.q.length; i >= numberOfQuestions; i--) {
            this.q.removeAt(i);
        }
    }
  }

  onSubmit() {
    this.submitted = true;

    var sbt ={
    title: this.quizzTitle,
		description: this.quizzIntro,
		keywords: this.quizzKW,
		questions: this.q.value
    }
    console.log(sbt);

    // stop here if form is invalid
    if (this.dynamicForm.invalid) {
        return;
    }

    this.req.submitQuizze(sbt).subscribe(
      data=>{
        alert('SUCCESS!! You have created a new quizze!! :-)\n\n' );
        setTimeout( () => this.router.navigate(['/admin']) , 100 );
      },(err : HttpErrorResponse)=>{}
    );

    // display form values on success
  
  }

  onReset() {
    // reset whole form back to initial state
    this.submitted = false;
    this.dynamicForm.reset();
    this.q.clear();
  }

  onClear() {
    // clear errors and reset quizze fields
    this.submitted = false;
    this.q.reset();
    this.dynamicForm.reset();
  }

}
