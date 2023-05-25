import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CaminhoComponent } from './caminho.component';

describe('CaminhoComponent', () => {
  let component: CaminhoComponent;
  let fixture: ComponentFixture<CaminhoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CaminhoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CaminhoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
