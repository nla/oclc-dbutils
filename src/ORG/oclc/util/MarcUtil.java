
package ORG.oclc.util;

import java.util.*;

import ORG.oclc.ber.*;

/** MarcUtil has the utilities to used in marc conversion.  It has static 
 *  methods to convert from frequency encoded tags to marc tags and vice/versa.
 *  It has defines for field, subfield, and record delimeters and it has
 *  methods to convert from numeric subfield tags to character tags and 
 *  vice/versa.  
 * @version @(#)MarcUtil.java	1.8 12/20/96
 * @author Lisa Cox
 */


public class MarcUtil {
    public final static char RECORD_TERMINATOR  = (char)0x1d;
    public final static char FIELD_TERMINATOR   = (char)0x1e;
    public final static char SUBFIELD_DELIMITER = (char)0x1f;

/** Array for changing marc tags to frequency encoded tags.
*/
        public static int marcToFrequency[];

/** Array for changing frequency encoded tags to Marc tags.
*/
        public static String frequencyToMarc[];



    public static char sub_char(int tag) {

        if(tag>=1  && tag <=26)
          return (char)(tag + 0x60);
        else
          if(tag >=27 && tag<=36)
           return (char)(tag+0x30-27);
          else
            if(tag>=37 && tag<=60)
              return (char)(tag+0x40-36);
            else
              return  ' ';
    }


    public static char sub_tag(String rec, int offset) {
	char c = rec.charAt(offset);
	return (char)((c >= 0x61 && c <= 0x7a) ? (c-0x60+0) :
		((c >= 0x30 && c <= 0x39) ? (c-0x30+27) :
		((c >= 0x41 && c<= 0x59) ? (c-0x40+36) : 255)));
    }


/* Table of frequency tag substitutes for marc tags */
/* order is frequency tag, value is marc tag */
/* f[3] = 245 --> marc tag 245 has a frequency encoded value of 3 */
    static final String f = 
/*  0- 15*/ "0 1 650 245 260 5 300 500 8 9 10 100 90 40 700 504 " +
/* 16- 31*/ "50 710 651 43 490 82 20 110 740 92 250 600 502 7 440 610 " +
/* 32- 47*/ "41 910 49 15 39 505 362 870 533 240 830 886 871 35 520 60 " +
/* 48- 63*/ "69 350 810 42 936 28 265 310 86 511 45 510 12 48 246 780 " +
/* 64- 79*/ "22 785 111 52 19 222 630 850 96 305 301 262 130 730 74 410 " +
/* 80- 95*/ "37 705 70 653 840 515 901 508 255 872 72 550 580 34 210 507 " +
/* 96-111*/ "518 306 752 990 920 501 880 261 711 88 546 873 263 715 503 47 " +
/*112-127*/ "11 890 773 242 570 321 760 16 55 536 33 247 800 30 775 212 " +
/*128-143*/ "25 555 51 776 699 32 530 525 66 787 611 17 541 513 538 506 " +
/*144-159*/ "772 753 535 27 521 400 652 655 770 302 24 777 545 80 254 18 " +
/*160-175*/ "534 583 811 930 351 765 547 243 762 540 44 911 411 656 561 512 " +
/*176-191*/ "767 581 543 516 315 91 981 556 523 359 308 257 73 23 2 3 " + 
/*192-207*/ "4 6 36 61 71 211 214 241 303 304 320 330 331 340 517 522 " +
/*208-223*/ "524 527 537 544 562 565 567 582 584 585 657 755 851 13 14 21 " +
/*224-239*/ "26 29 31 38 46 53 54 56 57 58 59 62 63 64 65 67 " +
/*240-255*/ "68 75 76 77 78 79 81 83 84 85 87 89 93 94 95 97 " +
/*256-271*/ "98 99 101 102 103 104 105 106 107 108 109 112 113 114 115 116 " +
/*272-287*/ "117 118 119 120 121 122 123 124 125 126 127 128 129 131 132 133 " +
/*288-303*/ "134 135 136 137 138 139 140 141 142 143 144 145 146 147 148 149 " +
/*304-319*/ "150 151 152 153 154 155 156 157 158 159 160 161 162 163 164 165 " +
/*320-335*/ "166 167 168 169 170 171 172 173 174 175 176 177 178 179 180 181 " +
/*336-351*/ "182 183 184 185 186 187 188 189 190 191 192 193 194 195 196 197 " +
/*352-367*/ "198 199 200 201 202 203 204 205 206 207 208 209 213 215 216 217 " +
/*368-383*/ "218 219 220 221 223 224 225 226 227 228 229 230 231 232 233 234 " +
/*384-399*/ "235 236 237 238 239 244 248 249 251 252 253 256 258 259 264 266 " +
/*400-415*/ "267 268 269 270 271 272 273 274 275 276 277 278 279 280 281 282 " +
/*416-431*/ "283 284 285 286 287 288 289 290 291 292 293 294 295 296 297 298 " +
/*432-447*/ "299 307 309 311 312 313 314 316 317 318 319 322 323 324 325 326 " +
/*448-463*/ "327 328 329 332 333 334 335 336 337 338 339 341 342 343 344 345 " +
/*464-479*/ "346 347 348 349 352 353 354 355 356 357 358 360 361 363 364 365 " +
/*480-495*/ "366 367 368 369 370 371 372 373 374 375 376 377 378 379 380 381 " +
/*496-511*/ "382 383 384 385 386 387 388 389 390 391 392 393 394 395 396 397 " +
/*512-527*/ "398 399 401 402 403 404 405 406 407 408 409 412 413 414 415 416 " +
/*528-543*/ "417 418 419 420 421 422 423 424 425 426 427 428 429 430 431 432 " +
/*544-559*/ "433 434 435 436 437 438 439 441 442 443 444 445 446 447 448 449 " +
/*560-575*/ "450 451 452 453 454 455 456 457 458 459 460 461 462 463 464 465 " +
/*576-591*/ "466 467 468 469 470 471 472 473 474 475 476 477 478 479 480 481 " +
/*592-607*/ "482 483 484 485 486 487 488 489 491 492 493 494 495 496 497 498 " +
/*608-623*/ "499 509 514 519 526 528 529 531 532 539 542 548 549 551 552 553 " +
/*624-639*/ "554 557 558 559 560 563 564 566 568 569 571 572 573 574 575 576 " +
/*640-655*/ "577 578 579 586 587 588 589 590 591 592 593 594 595 596 597 598 " +
/*656-671*/ "599 601 602 603 604 605 606 607 608 609 612 613 614 615 616 617 " +
/*672-687*/ "618 619 620 621 622 623 624 625 626 627 628 629 631 632 633 634 " +
/*688-703*/ "635 636 637 638 639 640 641 642 643 644 645 646 647 648 649 654 " +
/*704-719*/ "658 659 660 661 662 663 664 665 666 667 668 669 670 671 672 673 " +
/*720-735*/ "674 675 676 677 678 679 680 681 682 683 684 685 686 687 688 689 " +
/*736-751*/ "690 691 692 693 694 695 696 697 698 701 702 703 704 706 707 708 " +
/*752-767*/ "709 712 713 714 716 717 718 719 720 721 722 723 724 725 726 727 " +
/*768-783*/ "728 729 731 732 733 734 735 736 737 738 739 741 742 743 744 745 " +
/*784-799*/ "746 747 748 749 750 751 754 756 757 758 759 761 763 764 766 768 " +
/*800-815*/ "769 771 774 778 779 781 782 783 784 786 788 789 790 791 792 793 " +
/*816-831*/ "794 795 796 797 798 799 801 802 803 804 805 806 807 808 809 812 " +
/*832-847*/ "813 814 815 816 817 818 819 820 821 822 823 824 825 826 827 828 " +
/*848-863*/ "829 831 832 833 834 835 836 837 838 839 841 842 843 844 845 846 " +
/*864-879*/ "847 848 849 852 853 854 855 856 857 858 859 860 861 862 863 864 " +
/*880-895*/ "865 866 867 868 869 874 875 876 877 878 879 881 882 883 884 885 " +
/*896-911*/ "887 888 889 891 892 893 894 895 896 897 898 899 900 902 903 904 " +
/*912-927*/ "905 906 907 908 909 912 913 914 915 916 917 918 919 921 922 923 " +
/*928-943*/ "924 925 926 927 928 929 931 932 933 934 935 937 938 939 940 941 " +
/*944-959*/ "942 943 944 945 946 947 948 949 950 951 952 953 954 955 956 957 " +
/*960-975*/ "958 959 960 961 962 963 964 965 966 967 968 969 970 971 972 973 " +
/*976-991*/ "974 975 976 977 978 979 980 982 983 984 985 986 987 988 989 991 " +
/*992-999*/ "992 993 994 995 996 997 998 999";

    static {
	StringTokenizer ts = new StringTokenizer(f);
	int count = 1000, i; //ts.countTokens();
	marcToFrequency = new int[count];
	frequencyToMarc = new String[count];
	for (i=0; i<count; i++)
	{
	    frequencyToMarc[i] = ts.nextToken();
	    marcToFrequency[Integer.parseInt(frequencyToMarc[i])] = i;
	}
    }



}





