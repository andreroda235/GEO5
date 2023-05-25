import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PremErrComponent } from './premerr.component';

describe('NoPremErrComponent', () => {
  let component: PremErrComponent;
  let fixture: ComponentFixture<PremErrComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PremErrComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PremErrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
