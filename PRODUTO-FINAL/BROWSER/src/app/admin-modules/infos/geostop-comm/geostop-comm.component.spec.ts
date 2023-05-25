import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GeostopCommComponent } from './geostop-comm.component';

describe('GeostopCommComponent', () => {
  let component: GeostopCommComponent;
  let fixture: ComponentFixture<GeostopCommComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GeostopCommComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GeostopCommComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
