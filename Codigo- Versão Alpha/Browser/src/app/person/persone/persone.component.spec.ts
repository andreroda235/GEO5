import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PersoneComponent } from './persone.component';

describe('PersoneComponent', () => {
  let component: PersoneComponent;
  let fixture: ComponentFixture<PersoneComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PersoneComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PersoneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
