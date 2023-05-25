import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormpolilineComponent } from './formpoliline.component';

describe('FormpolilineComponent', () => {
  let component: FormpolilineComponent;
  let fixture: ComponentFixture<FormpolilineComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormpolilineComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormpolilineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
